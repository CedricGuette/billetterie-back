package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import com.jeuxolympiques.billetterie.services.VerificationPhotoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moderators")
@RequiredArgsConstructor
@CrossOrigin(origins = "${URL_FRONT}")
public class ModeratorController {

    private final ModeratorService moderatorService;
    private final VerificationPhotoService verificationPhotoService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

    /**
     * Requête pour récupérer l'ensemble des photos de vérification et les données correspondantes
     * @param token Pour connaitre l'identité du modérateur
     * @return List<> des photos de vérification
     */
    @GetMapping
    @JsonView(Views.Moderator.class)
    ResponseEntity<List<VerificationPhoto>> getAllVerificationPhoto(@RequestHeader(name="Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7));
        Moderator moderator = moderatorService.getModeratorByUsername(username);

        logger.info(STR."Le modérateur \{moderator.getUsername()} est connecté sur la page de vérification des photos.");

        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(moderatorService.getAllVerificationPhotos());
    }

    /**
     * Requête pour valider une photo de vérification
     * @param id Identifiant de la photo de vérification à valider
     * @param token Pour noter l'identité du modérateur qui valide l'image
     * @return
     * @throws IOException
     */
    @PatchMapping("/{id}")
    ResponseEntity<Map<String, String>>photoValidationById(@PathVariable String id, @RequestHeader(name="Authorization") String token) throws IOException {

        // On récupère les informations par le token pour noter le modérateur qui valide la photo
        String username = jwtUtils.extractUsername(token.substring(7));

        // On récupère le client pour le log et le modérateur
        Customer customer = verificationPhotoService.getCustomerFromVerificationPhotoId(id);
        Moderator moderator = moderatorService.getModeratorByUsername(username);

        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = moderatorService.photoValidationById(id, username);

        logger.info(STR."Le compte \{customer.getUsername()} a bien été validé pa \{moderator.getUsername()}, la photo a été supprimée.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }
}
