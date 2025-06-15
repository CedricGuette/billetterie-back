package com.jeuxolympiques.billetterie.controllers;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.services.TicketService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class CheckoutController {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Value("${URL_FRONT}")
    private String urlFront;

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final JwtUtils jwtUtils;
    private HttpHeadersCORS httpHeaders = new HttpHeadersCORS();

    /*
    * Requête pour lancer une session de paiement Stripe
    */
    @PostMapping("/checkout/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> checkout(@RequestHeader(name="Authorization") String token, @PathVariable String id) throws StripeException {

        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On cherche le ticket correspondant à l'id de la requête
        Optional<Ticket> ticket;
        ticket = ticketRepository.findById(id);

        // On crée le corps de la réponse
        Map<String, String> response = new HashMap<>();

        // On vérifie la cohérence des informations
        if(!ticket.get().getCustomer().getId().equals(user.getId())) {
            response.put("error", "Le numéro de ticket et l'utilisateur ne correspondent pas");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(httpHeaders.headers())).body(response);
        }

        // Si le ticket existe
        if(ticket.isPresent()) {
            int amount;
            switch (ticket.get().getHowManyTickets()) {
                case 1 : amount = 5000;
                break;

                case 2 : amount = 9000;
                break;

                case 4 : amount = 16000;
                break;

                default: response.put("error", "Le nombre de ticket enregistré ne correspond pas au champs du possible, veuillez contacter le support.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(httpHeaders.headers())).body(response);
            }

            // On nomme le ticket
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Ticket numéro: " + ticket.get().getId() + " " + ticket.get().getCustomer().getFirstName() + " " + ticket.get().getCustomer().getLastName())
                    .build();

            // On donne une valeur au ticket
            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("EUR")
                    .setUnitAmount(Integer.toUnsignedLong(amount))
                    .setProductData(productData)
                    .build();

            // On donne la quantité de une (offre) au "panier"
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();

            // On crée une session avec les éléments adéquats
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setUiMode(SessionCreateParams.UiMode.CUSTOM)
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setReturnUrl(urlFront + "/return/{CHECKOUT_SESSION_ID}/" + ticket.get().getId())
                            .addLineItem(lineItem)
                            .build();

            Session session = Session.create(params);

            //ticket.get().setSessionId(session.getId()); => dans la BDD
            //ticketRepository.save(ticket);

            // On insert les élément de la réponse à la requête
            response.put("checkoutSessionClientSecret", session.getClientSecret());

            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(response);

        }
        // Sinon on renvoie une erreur
        response.put("error", "Ticket non trouvé.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body(response);
    }

    /*
    * Requête pour vérifier que le paiement a bien été exécuté et donc pour lancer la conception du ticket
    */
    @GetMapping("checkout/validation/{sessionId}/{ticketId}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> isCheckoutOk(@RequestHeader(name="Authorization") String token,
                                                            @PathVariable("sessionId") String sessionId,
                                                            @PathVariable("ticketId") String ticketId) throws StripeException, IOException, NoSuchAlgorithmException, WriterException {

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On récupère le ticket
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        // On crée le corps de la réponse
        Map<String, String> response = new HashMap<>();

        if(!ticket.isPresent()) {
            response.put("error", "Le ticket n'a pas été trouvé dans la base de données.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body(response);
        }

        if(!ticket.get().getCustomer().getId().equals(user.getId())) {
            response.put("error", "Le ticket et l'utilisateur ne correspondent pas.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(httpHeaders.headers())).body(response);
        }

        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("line_items").build();

        Session checkoutSession = Session.retrieve(sessionId, params, null);

        if(checkoutSession.getPaymentStatus() != "unpaid") {

            response.put("checkoutStatus", "Le paiement à bien été effectué!");
            ticketService.ticketPayed(ticket.get().getId());
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(response);
        }

        response.put("checkoutStatus", "Le paiement n'a pas été effectué, nous vous prions de bien vouloir recommencer.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(String.valueOf(httpHeaders.headers())).body(response);
    }

}
