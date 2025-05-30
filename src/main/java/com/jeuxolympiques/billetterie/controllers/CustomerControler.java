package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
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

import java.util.List;
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

    @GetMapping()
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.User.class)
    public ResponseEntity<?> retrieveCustomerInfo(@RequestHeader(name="Authorization") String token) {
        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        Optional<Customer> customer = customerRepository.findById(user.getId());

        if(customer.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(customer);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body("Utilisateur non trouvé.");
    }

    @GetMapping("/tickets")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> getAllTickets(@RequestHeader(name="Authorization") String token) {
        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        List<Ticket> listOfTicketsFromCustomersId;
        listOfTicketsFromCustomersId = customerService.listOfTicketsFromCustomersId(user.getId());

        // On vérifie qu'on a bien récupéré quelque chose
        if(!listOfTicketsFromCustomersId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(listOfTicketsFromCustomersId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body("Aucun ticket n'a été trouvé.");
    }

    @GetMapping("/cartprice/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> cartPrice(@RequestHeader(name="Authorization") String token, @PathVariable String id) {
        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        Optional<Ticket> ticket = ticketRepository.findById(id);

        if(ticket.isPresent()) {
            Optional<Customer> customer = customerRepository.findById(user.getId());

            if (customer.isPresent()) {

                if(!customer.get().getTickets().contains(ticket.get())){

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(String.valueOf(httpHeaders.headers())).body("La requête n'a pas put aller au bout.");
                }

                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(ticket.get().getHowManyTickets());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(String.valueOf(httpHeaders.headers())).body("La requête n'a pas put aller au bout.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(String.valueOf(httpHeaders.headers())).body("La requête n'a pas put aller au bout.");
    }
}
