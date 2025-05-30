package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderators")
@RequiredArgsConstructor
public class ModeratorControler {

    private final ModeratorService moderatorService;
    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();

    @GetMapping
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.Moderator.class)
    ResponseEntity<List<VerificationPhoto>> getAllVerificationPhoto() {
        // On appelle la fonction pour chercher toutes les photos depuis les services de Moderator
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(moderatorService.getAllVerificationPhoto());
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<?> getVerificationPhotoById(@PathVariable String id) {
        // On cherche si la photo existe depuis les services de Moderator
        Optional<VerificationPhoto> verificationPhoto = Optional.ofNullable(moderatorService.getVerificationPhotoById(id));
        // Si la photo existe la sélectioner
        if(verificationPhoto.isPresent()) {
            return  ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body(verificationPhoto.get());
        }
        // Sinon renvoyer une erreur
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body("La photo recherchée n'a pas été trouvée");
    }

    @PatchMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    ResponseEntity<String> photoValidationById(@PathVariable String id, @RequestHeader(name="Authorization") String token) throws IOException {
        String username = jwtUtils.extractUsername(token.substring(7));
        if (moderatorService.photoValidationById(id, username)) {
            return  ResponseEntity.status(HttpStatus.OK).header(String.valueOf(httpHeaders.headers())).body("La compte a bien été validé, la photo a été supprimée.");
        }
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(httpHeaders.headers())).body("Le compte n'a pas été trouvé.");
    }
}
