package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityControler {

    private final SecurityService securityService;
    private final JwtUtils jwtUtils;
    private HttpHeadersCORS headersCORS = new HttpHeadersCORS();

    @GetMapping("/{ticketCode}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<String>> isTicketValid(@PathVariable String ticketCode, @RequestHeader(name="Authorization") String token) throws NoSuchAlgorithmException {
        String username = jwtUtils.extractUsername(token.substring(7));
        Integer responseCode = securityService.isThisTicketValid(ticketCode, username);
        List<String> response = new ArrayList<>();
        switch (responseCode) {
            // Le billet est valide et non utilisé
            case 0 :
                response.add("Le ticket est validé !");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
            // le billet est valide mais a déjà été utilisé
            case 1:
                response.add("Le ticket a déjà été utilisé");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
            // l'identifiant et le hash ne sont pas en accord
            case 2:
                response.add("L'identifiant ne correspond pas avec le reste du code");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
            //L'identifiant est introuvable dans la base de données
            default:
                response.add("L'identifiant ne correspond à aucun élément connu");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
        }
    }
}
