package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerControler {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CustomerControler.class);

    // On met les réponses dans des variables
    private static final String ERRORJSON = "error";
    private static final String USER_NOT_FOUND = "Utilisateur non trouvé.";

    private static final String CUSTOMER_CONNECTED = "Un client est connecté.";
    private static final String USER_NOT_FOUND_LOGGER = "Un utilisateur n'existant pas a été demandé.";

    /*
    * Requête pour récupérer les informations du client connecté
    */
    @GetMapping()
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.User.class)
    public ResponseEntity<?> retrieveCustomerInfo(@RequestHeader(name="Authorization") String token) {

        // On récupère l'information du token pour savoir quel utilisateur est connecté
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        Optional<Customer> customer = customerRepository.findById(user.getId());

        // Si on trouve bien l'utilisateur en base de données
        if(customer.isPresent()) {
            // On renvoit l'objet client pour remplir le profil utilisateur
            logger.info(CUSTOMER_CONNECTED);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(String.valueOf(httpHeaders.headers()))
                    .body(customer);
        }
        // sinon on renvoie une erreur
        Map<String, String> response = new HashMap<>();
        response.put(ERRORJSON, USER_NOT_FOUND);
        logger.error(USER_NOT_FOUND_LOGGER);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }
}
