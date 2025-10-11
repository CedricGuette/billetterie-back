package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.Views;

import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "${URL_FRONT}")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final EventService eventService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /**
     * Requête pour récupérer les informations du client connecté
     * @param token Pour savoir quel client requête et lui envoyer les informations adéquates
     * @return
     */
    @GetMapping()
    @JsonView(Views.Customer.class)
    public ResponseEntity<Customer> retrieveCustomerInfo(@RequestHeader(name="Authorization") String token) {

        // On récupère l'information du token pour savoir quel utilisateur est connecté
        String username = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerService.getCustomerByUsername(username);

        // On renvoie l'objet client pour remplir le profil utilisateur
        logger.info(STR."Le client \{customer.getUsername()} est connecté sur sa page de profil.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(customer);
    }

    /**
     * Requête pour qu'un client ayant déjà un compte puisse acheter un ticket pour un autre évènement
     * @param token Pour connaitre l'identité du client
     * @param id Identifiant de l'évènement du nouveau ticket
     * @param ticket Information du nombre de places
     * @return
     */
    @PostMapping("/buy/{id}")
    public  ResponseEntity<Map<String, String>> customerBuyAnotherTicket(@RequestHeader(name="Authorization") String token, @PathVariable String id, @RequestBody Ticket ticket){
        // On récupère l'information du token pour savoir quel utilisateur est connecté
        String username = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerService.getCustomerByUsername(username);
        Event eventInDatabase = eventService.getEventById(id);
        customerService.createNewTicket(ticket, customer, eventInDatabase);

        Map<String, String> response = new HashMap<>();
        response.put("created", "Votre ticket a bien été créé.");

        // On renvoie l'objet client pour remplir le profil utilisateur
        logger.info(STR."Le client \{customer.getUsername()} achète un ticket pour \{eventInDatabase.getName()}");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }
}
