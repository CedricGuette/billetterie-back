package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moderators")
@RequiredArgsConstructor
public class ModeratorControler {

    private final ModeratorService moderatorService;
    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(ModeratorControler.class);

    // On met les réponses dans des variables
    private static final String VALIDATED = "La compte a bien été validé, la photo a été supprimée.";
    private static final String USER_NOT_FOUND = "Le compte n'a pas été trouvé.";

    private static final String ERRORJSON = "error";
    private static final String VALIDATEDJSON = "validated";

    /*
    * Requête pour récupérer l'ensemble des photos de vérifications et les données correspondantes
    */
    @GetMapping
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.Moderator.class)
    ResponseEntity<List<VerificationPhoto>> getAllVerificationPhoto() {

        // On appelle la fonction pour chercher toutes les photos depuis les services de Moderator
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(moderatorService.getAllVerificationPhoto());
    }

    /*
    * Requête pour valider une photo de vérification
    */
    @PatchMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<Map<String, String>>photoValidationById(@PathVariable String id, @RequestHeader(name="Authorization") String token) throws IOException {
        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();

        // On récupère les informations par le token pour noter le modérateur qui valide la photo
        String username = jwtUtils.extractUsername(token.substring(7));
        if (Boolean.TRUE.equals(moderatorService.photoValidationById(id, username))) {
            response.put(VALIDATEDJSON, VALIDATED);
            logger.info(VALIDATED);

            return  ResponseEntity
                    .status(HttpStatus.OK)
                    .header(String.valueOf(httpHeaders.headers()))
                    .body(response);
        }
        response.put(ERRORJSON, USER_NOT_FOUND);
        logger.error(USER_NOT_FOUND);

        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }
}
