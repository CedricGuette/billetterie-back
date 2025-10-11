package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.exceptions.FileNotFoundException;
import com.jeuxolympiques.billetterie.exceptions.UnauthorizedFileAccessException;
import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.ModeratorService;
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

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@CrossOrigin(origins = "${URL_FRONT}")
public class ResourcesOnServerController {

    private final ModeratorService moderatorService;
    private final CustomerService customerService;

    private final JwtUtils jwtUtils ;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(ResourcesOnServerController.class);

    /**
     * Requête pour récupérer les photos de vérification
     * @param filename Nom du fichier à récupérer sur le serveur
     * @param token Pour vérifier l'identité du requêteur
     * @return Le fichier demandé
     */
    @GetMapping("/uploads/verification/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename, @RequestHeader(name="Authorization") String token) {

        String username = jwtUtils.extractUsername(token.substring(7));
        Moderator moderator = moderatorService.getModeratorByUsername(username);

        logger.info(STR."Le modérateur \{moderator.getUsername()} essaye de récupérer l'image \{filename}.");

        try {
            Path filePath = Paths.get("src/main/resources/static/uploads/verification/").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {

                logger.info(STR."L'image \{filename} a été récupéré avec succès par le modérateur \{moderator.getUsername()}.");

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(resource);
            } else {
                throw new FileNotFoundException(STR."L'image \{filename} recherchée pas le modérateur \{moderator.getUsername()} n'a pas été trouvée.");
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(String.valueOf(headersCORS.headers()))
                    .build();
        }
    }

    /**
     * Requête pour récupérer les photos des évènements
     * @param filename Nom du fichier
     * @return Image demandée
     */
    @GetMapping("/uploads/event/{filename}")
    public ResponseEntity<Resource> getEventImage(@PathVariable String filename) {

        try {
            Path filePath = Paths.get("src/main/resources/static/uploads/event/").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(resource);
            } else {
                throw new FileNotFoundException(STR."L'image \{filename} n'a pas été trouvée.");
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(String.valueOf(headersCORS.headers()))
                    .build();
        }
    }

    /**
     * Requête pour récupérer les tickets en PDF
     * @param filename Nom du fichier
     * @param token Pour vérifier l'identité du requêteur
     * @return Le pdf demandé
     */
    @GetMapping("/tickets/pdf/{filename}")
    public ResponseEntity<Resource> getPdf(@PathVariable String filename, @RequestHeader(name="Authorization") String token) {

        // On récupère les informations pour savoir qui veut consulter le pdf
        String username = jwtUtils.extractUsername(token.substring(7));

        Customer customer = customerService.getCustomerByUsername(username);

        List<Ticket> tickets = customer.getTickets();

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
                    throw new FileNotFoundException(STR."Le PDF \{filename} recherchée pas le client \{customer.getUsername()} n'a pas été trouvée.");
                }
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header(String.valueOf(headersCORS.headers()))
                        .build();
            }
        }
        throw new UnauthorizedFileAccessException(STR."L'utilisateur \{customer.getUsername()} essaie d'accèder à un ticket en pdf sans l'autorisation.");
    }
}
