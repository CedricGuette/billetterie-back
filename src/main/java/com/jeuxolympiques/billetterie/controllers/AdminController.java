package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.services.AdminService;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import com.jeuxolympiques.billetterie.services.SecurityService;
import com.jeuxolympiques.billetterie.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ModeratorService moderatorService;
    private final SecurityService securityService;
    private final AdminService adminService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /*
     * On crée la requête pour récupérer la liste des utilisateurs
     */
    @GetMapping("/users")
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.Admin.class)
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("L'administrateur a récupéré l'ensemble des utilisateurs");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(userService.getAllUsers());
    }

    /*
     * On crée la requête pour créer un modérateur
     */
    @PostMapping("/createModerator")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> createModerator(@RequestBody Moderator moderator) {

        //On crée la varaible qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();
        response.put("created", "Le modérateur a bien été créé.");

        moderatorService.createModerator(moderator);

        logger.info(STR."le modérateur \{moderator.getUsername()} a été créé.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
    * On crée la requête pour créer un agent de sécurité
    */
    @PostMapping("/createSecurity")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> createSecurity(@RequestBody Security security) {

        securityService.createSecurity(security);

        // On crée la variable qui va accueillir la réponse
        Map<String, String> response = new HashMap<>();
        response.put("created", "L'agent de sécurité a bien été créé.");

        logger.info(STR."L'agent de sécurité \{security.getUsername()} a bien été créé.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
     * On crée la requête pour effacer un utilisateur
     */
    @DeleteMapping("users/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> deleteUserById (@PathVariable String id) {
        // On récupère l'utilisateur grâce à l'id
        User user = userService.getUserById(id);

        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = adminService.deleteUser(user);

        logger.info(STR."L'utilisateur \{user.getUsername()} a bien été supprimé.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }
}
