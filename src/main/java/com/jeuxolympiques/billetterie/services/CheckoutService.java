package com.jeuxolympiques.billetterie.services;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.exceptions.CheckoutNotPayedException;
import com.stripe.Stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Value("${URL_FRONT}")
    private String urlFront;

    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    public Map<String, String> checkoutSessionStart(Ticket ticket) throws StripeException {
        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        // On crée le corps de la réponse
        Map<String, String> response = new HashMap<>();
        Session session;

        // On vérifie qu'il n'y a pas déjà une session de paiement ou qu'elle date d'il y a moins de 24h
        if(ticket.getSessionId() == null || ticket.getSessionCreatedDate().isAfter(ticket.getSessionCreatedDate().plusDays(1))){

            int amount = switch (ticket.getHowManyTickets()) {
                case 1 -> 5000;
                case 2 -> 9000;
                case 4 -> 16000;
                default ->
                        throw new IllegalArgumentException("Le nombre de ticket enregistré ne correspond pas au champs du possible, veuillez contacter le support.");
            };

            // On nomme le ticket
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(STR."Ticket numéro: \{ticket.getId()} \{ticket.getCustomer().getFirstName()} \{ticket.getCustomer().getLastName()}")
                    .build();

            // On donne une valeur au ticket
            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("EUR")
                    .setUnitAmount(Integer.toUnsignedLong(amount))
                    .setProductData(productData)
                    .build();

            // On donne la quantité d'une (offre) au panier
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();

            // On crée une session avec les éléments adéquats
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setUiMode(SessionCreateParams.UiMode.CUSTOM)
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setReturnUrl(STR."\{urlFront}/return/\{ticket.getId()}")
                            .addLineItem(lineItem)
                            .build();

            session = Session.create(params);

            ticket.setSessionId(session.getId());
            ticket.setSessionClientSecret(session.getClientSecret());
            ticket.setSessionCreatedDate(LocalDateTime.now());
            ticketService.updateTicket(ticket);

            // On insère les éléments de la réponse à la requête
            response.put("checkoutSessionClientSecret", session.getClientSecret());
            logger.info("Création d'une nouvelle session de paiement.");
        } else {
            // On insère les éléments de la réponse à la requête
            response.put("checkoutSessionClientSecret", ticket.getSessionClientSecret());
            logger.info("Récupération d'une session de paiement existante.");
        }

        return response;
    }

    public Map<String, String> isCheckoutPayed(Ticket ticket) throws IOException, NoSuchAlgorithmException, WriterException, StripeException {
        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();

        SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("line_items").build();

        Session checkoutSession = Session.retrieve(ticket.getSessionId(), params, null);

        if (ticket.getTicketIsPayed().equals(true)){
            response.put("checkoutStatus", "Le paiement a déjà été effectué!");

            return response;

        }else if(!checkoutSession.getPaymentStatus().equals("unpaid") && ticket.getTicketIsPayed().equals(false)) {

            response.put("checkoutStatus", "Le paiement a bien été effectué!");
            ticketService.ticketPayed(ticket.getId());

            return response;
        }
        throw new CheckoutNotPayedException("Le paiement n'a pas été effectué, nous vous prions de bien vouloir recommencer.");
    }
}
