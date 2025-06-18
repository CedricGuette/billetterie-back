package com.jeuxolympiques.billetterie.controllers;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.services.CheckoutService;
import com.jeuxolympiques.billetterie.services.TicketService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    private final CheckoutService checkoutService;
    private final TicketService ticketService;
    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    // On met les réponses dans des variables
    private static final String TICKET_NOT_FOUND = "Le ticket n'a pas été trouvé dans la base de données.";
    private static final String TICKET_AND_USER_DONT_MATCH = "Le ticket et l'utilisateur ne correspondent pas.";

    private static final String ERRORJSON = "error";

    private static final String SESSION_CREATED = "Une session de paiement Stripe a bien été créée.";
    private static final String NOT_PAYED = "Le paiement du ticket n'a pas été effectué.";
    private static final String PAYED = "Le ticket a bien été payé.";
    private static final String TICKET_NOT_FOUND_CREATE_SESSION = "Le ticket n'a pas été trouvé dans la base de données lors de la création de session de paiement.";
    private static final String TICKET_NOT_FOUND_CHECK_PAYED = "Le ticket n'a pas été trouvé dans la base de données lors de la vérification de paiement.";
    private static final String TICKET_AND_USER_DONT_MATCH_CREATE_SESSION = "Le ticket et l'utilisateur ne correspondent pas lors de la création de session de paiement.";
    private static final String TICKET_AND_USER_DONT_MATCH_CHECK_PAYED = "Le ticket et l'utilisateur ne correspondent pas lors de la vérification de paiement.";

    /*
    * Requête pour lancer une session de paiement Stripe
    */
    @PostMapping("/checkout/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> checkout(@RequestHeader(name="Authorization") String token, @PathVariable String id) throws StripeException {

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On cherche le ticket correspondant à l'id de la requête
        Optional<Ticket> ticket;
        ticket = ticketRepository.findById(id);

        // On crée le corps de la réponse
        Map<String, String> response = new HashMap<>();


        if(ticket.isPresent()){

            // On vérifie la cohérence des informations
            if(!ticket.get().getCustomer().getId().equals(user.getId())) {
                response.put(ERRORJSON, TICKET_AND_USER_DONT_MATCH);
                logger.error(TICKET_AND_USER_DONT_MATCH_CREATE_SESSION);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header(String.valueOf(httpHeaders.headers()))
                        .body(response);
            }

            logger.info(SESSION_CREATED);
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(checkoutService.checkoutSessionStart(ticket.get()));
        }

        // Sinon on renvoie une erreur
        response.put(ERRORJSON, TICKET_NOT_FOUND);
        logger.error(TICKET_NOT_FOUND_CREATE_SESSION);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
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

        // On vérifie que le ticket existe
        if(ticket.isPresent()) {

            // On vérifie que le ticket appartient bien à l'utilisateur connecté
            if(ticket.get().getCustomer().getId().equals(user.getId())) {

                // On vérifie qu'on ne renvoie pas une erreur
                if(checkoutService.isCheckoutPayed(sessionId, ticket.get()).containsKey(ERRORJSON)) {
                    logger.error(NOT_PAYED);

                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(String.valueOf(httpHeaders.headers()))
                            .body(checkoutService.isCheckoutPayed(sessionId, ticket.get()));
                }
                logger.info(PAYED);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .header(String.valueOf(httpHeaders.headers()))
                        .body(checkoutService.isCheckoutPayed(sessionId, ticket.get()));
            }

            response.put(ERRORJSON, TICKET_AND_USER_DONT_MATCH);
            logger.error(TICKET_AND_USER_DONT_MATCH_CHECK_PAYED);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header(String.valueOf(httpHeaders.headers()))
                    .body(response);
        }

        response.put(ERRORJSON, TICKET_NOT_FOUND);
        logger.error(TICKET_NOT_FOUND_CHECK_PAYED);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);

    }

}
