package com.jeuxolympiques.billetterie.services;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.stripe.Stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

    public Map<String, String> checkoutSessionStart(Ticket ticket) throws StripeException {
        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        // On crée le corps de la réponse
        Map<String, String> response = new HashMap<>();

        int amount;
        switch (ticket.getHowManyTickets()) {
            case 1:
                amount = 5000;
                break;

            case 2:
                amount = 9000;
                break;

            case 4:
                amount = 16000;
                break;

            default:
                throw new IllegalArgumentException("Le nombre de ticket enregistré ne correspond pas au champs du possible, veuillez contacter le support.");
        }

        // On nomme le ticket
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Ticket numéro: " + ticket.getId() + " " + ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName())
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
                        .setReturnUrl(urlFront + "/return/{CHECKOUT_SESSION_ID}/" + ticket.getId())
                        .addLineItem(lineItem)
                        .build();

        Session session = Session.create(params);

        // On insert les élément de la réponse à la requête
        response.put("checkoutSessionClientSecret", session.getClientSecret());

        return response;
    }

    public Map<String, String> isCheckoutPayed(String sessionId, Ticket ticket) throws IOException, NoSuchAlgorithmException, WriterException, StripeException {
        // On initialise Stripe avec la clef
        Stripe.apiKey = stripeSecretKey;

        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();

        SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("line_items").build();

        Session checkoutSession = Session.retrieve(sessionId, params, null);

        if(checkoutSession.getPaymentStatus() != "unpaid") {

            response.put("checkoutStatus", "Le paiement à bien été effectué!");
            ticketService.ticketPayed(ticket.getId());

            return response;
        }

        response.put("checkoutStatus", "Le paiement n'a pas été effectué, nous vous prions de bien vouloir recommencer.");
        return response;
    }
}

//ticket.get().setSessionId(session.getId()); => dans la BDD
//ticketRepository.save(ticket);