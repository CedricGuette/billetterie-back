package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.services.CustomerService;
import lombok.RequiredArgsConstructor;
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
    private final TicketRepository ticketRepository;
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();

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
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(customer);
        }
        // sinon on renvoie une erreur
        Map<String, String> response = new HashMap<>();
        response.put("error", "Utilisateur non trouvé.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body(response);
    }
}
