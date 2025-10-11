package com.jeuxolympiques.billetterie.controllers;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.exceptions.CustomerAndTicketNotMatchException;
import com.jeuxolympiques.billetterie.services.CheckoutService;
import com.jeuxolympiques.billetterie.services.CustomerService;
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

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "${URL_FRONT}")
public class CheckoutController {

    private final CustomerService customerService;
    private final TicketService ticketService;

    private final CheckoutService checkoutService;
    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    /**
     * Requête pour lancer une session de paiement Stripe
     * @param token Pour connaitre l'utilisateur qui requête
     * @param id Identifiant du ticket pour lequel on veut ouvrir une session de paiement
     * @return
     * @throws StripeException
     */
    @PostMapping("/checkout/{id}")
    public ResponseEntity<Map<String, String>> checkout(@RequestHeader(name="Authorization") String token, @PathVariable String id) throws StripeException {

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerService.getCustomerByUsername(username);

        // On cherche le ticket correspondant à l'id de la requête
        Ticket ticket = ticketService.getTicketById(id);

        // On vérifie la cohérence des informations
        if(!ticket.getCustomer().getId().equals(customer.getId())) {
            throw new CustomerAndTicketNotMatchException("Le ticket et l'utilisateur ne correspondent pas.");
        }

        logger.info("Une session de paiement Stripe est en cours.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(checkoutService.checkoutSessionStart(ticket));
    }

    /**
     * Requête pour vérifier que le paiement a bien été exécuté et donc pour lancer la conception du ticket
     * @param token Pour connaitre l'utilisateur qui requête
     * @param ticketId Identifiant du ticket concerné
     * @return
     * @throws StripeException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws WriterException
     */
    @GetMapping("checkout/validation/{ticketId}")
    public ResponseEntity<Map<String, String>> isCheckoutOk(@RequestHeader(name="Authorization") String token,
                                                            @PathVariable("ticketId") String ticketId) throws StripeException, IOException, NoSuchAlgorithmException, WriterException {

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerService.getCustomerByUsername(username);

        // On récupère le ticket
        Ticket ticket = ticketService.getTicketById(ticketId);

        // On vérifie que le ticket appartient bien à l'utilisateur connecté
        if(ticket.getCustomer().getId().equals(customer.getId())) {

            logger.info(STR."Le ticket de \{customer.getUsername()} a bien été payé.");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(String.valueOf(httpHeaders.headers()))
                    .body(checkoutService.isCheckoutPayed(ticket));
        }

        throw new CustomerAndTicketNotMatchException("Le ticket et l'utilisateur ne correspondent pas.");
    }
}
