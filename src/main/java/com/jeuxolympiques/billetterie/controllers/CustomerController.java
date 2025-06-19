package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Views;

import com.jeuxolympiques.billetterie.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /*
    * Requête pour récupérer les informations du client connecté
    */
    @GetMapping()
    @JsonView(Views.Customer.class)
    public ResponseEntity<Customer> retrieveCustomerInfo(@RequestHeader(name="Authorization") String token) {

        // On récupère l'information du token pour savoir quel utilisateur est connecté
        String username = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerService.getCustomerByUsername(username);

        // On renvoit l'objet client pour remplir le profil utilisateur
        logger.info(STR."Le client \{customer.getUsername()} est connecté sur sa page de profil.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(customer);
    }
}
