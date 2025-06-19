package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Security;
import com.jeuxolympiques.billetterie.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    /*
    *  Requête pour vérifier et valider un ticket
    */
    @GetMapping("/{ticketCode}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> isTicketValid(@PathVariable String ticketCode, @RequestHeader(name="Authorization") String token) throws NoSuchAlgorithmException {

        // On récupère les information de l'agent de sécurité qui scan
        String username = jwtUtils.extractUsername(token.substring(7));

        // On récupère les informations pour le log
        Security security = securityService.getSecurityByUsername(username);

        // On démande au service de comparer les informations et de renvoyer la réponse
        Map<String, String> response = securityService.isThisTicketValid(ticketCode, username);
        logger.info(STR."L'agent \{security.getUsername()} de sécurité a scanné un ticket.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }
}
