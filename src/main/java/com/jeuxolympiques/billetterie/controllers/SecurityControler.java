package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityControler {

    private final SecurityService securityService;
    private final JwtUtils jwtUtils;
    private HttpHeadersCORS headersCORS = new HttpHeadersCORS();

    /*
    *  Requête pour vérifier et valider un ticket
    */
    @GetMapping("/{ticketCode}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> isTicketValid(@PathVariable String ticketCode, @RequestHeader(name="Authorization") String token) throws NoSuchAlgorithmException {

        // On récupère les information de l'agent de sécurité qui scan
        String username = jwtUtils.extractUsername(token.substring(7));

        // On démande au service de comparer les informations et de renvoyer la réponse
        Map<String, String> response = securityService.isThisTicketValid(ticketCode, username);
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
    }
}
