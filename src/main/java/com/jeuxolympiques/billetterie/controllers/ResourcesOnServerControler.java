package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ResourcesOnServerControler {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private final JwtUtils jwtUtils ;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(ResourcesOnServerControler.class);

    private static final String UNAUTHORIZED_PDF = "Quelqu'un essaie d'accèder à un ticket en pdf sans l'autorisation.";
    private static final String NOT_FOUND_PDF = "Quelqu'un cherche un ticket en pdf qui n'existe pas.";

    /*
    * Requête pour récupérer les photos de vérification
    */
    @GetMapping("/uploads/{filename}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("src/main/resources/static/uploads/").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(resource);
            } else {

                return ResponseEntity
                        .notFound()
                        .header(String.valueOf(headersCORS.headers()))
                        .build();
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(String.valueOf(headersCORS.headers()))
                    .build();
        }
    }

    /*
    * Requête pour récupérer les tickets en PDF
    */
    @GetMapping("/tickets/pdf/{filename}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Resource> getPdf(@PathVariable String filename, @RequestHeader(name="Authorization") String token) {

        // On récupère les informations pour savoir qui veut consulter le pdf
        String username = jwtUtils.extractUsername(token.substring(7));

        User user = userRepository.findByUsername(username);

        Optional<Customer> customer = customerRepository.findById(user.getId());

        if(customer.isPresent()){

            List<Ticket> tickets = customer.get().getTickets();

            List<Ticket> result = tickets.stream()
                    .filter(ticket -> ticket.getTicketUrl().equals(STR."tickets/pdf/\{filename}"))
                    .toList();

            if(!(result.isEmpty())) {


                try {
                    Path filePath = Paths.get("src/main/resources/static/tickets/pdf/").resolve(filename);
                    Resource resource = new UrlResource(filePath.toUri());

                    if (resource.exists()) {

                        return ResponseEntity
                                .ok()
                                .contentType(MediaType.APPLICATION_PDF)
                                .header(String.valueOf(headersCORS.headers()))
                                .body(resource);
                    } else {

                        return ResponseEntity
                                .notFound()
                                .header(String.valueOf(headersCORS.headers()))
                                .build();
                    }
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage());

                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header(String.valueOf(headersCORS.headers()))
                            .build();
                }
            }
            logger.error(UNAUTHORIZED_PDF);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(String.valueOf(headersCORS.headers()))
                    .build();
        }
        logger.error(NOT_FOUND_PDF);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .build();
    }
}
