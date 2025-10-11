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
@CrossOrigin(origins = "${URL_FRONT}")
public class AdminController {

    private final UserService userService;
    private final ModeratorService moderatorService;
    private final SecurityService securityService;
    private final AdminService adminService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * On crée la requête pour récupérer la liste des utilisateurs
     * @return ResponseEntity liste des utilisateurs
     */
    @GetMapping("/users")
    @JsonView(Views.Admin.class)
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("L'administrateur a récupéré l'ensemble des utilisateurs");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(userService.getAllUsers());
    }

    /**
     * Requête pour renvoyer l'information si l'administrateur se connecte pour la première fois
     * @param token jeton JWT
     * @return Booléen vrai si l'administrateur se connecte pour la première fois
     */
    @GetMapping("/firstLogin")
    public ResponseEntity<Boolean> isFirstLogin(@RequestHeader(name="Authorization") String token){
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userService.getUserByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(adminService.isFirstLogin(user.getId()));

    }

    /**
     * On crée la requête pour changer de mot de passe
     * @param request objet envoyé contenant l'ancien et le nouveau mot de passe
     * @param token jeton JWT
     * @return ResponseEntity updated
     */
    @PutMapping("/password/change")
    public ResponseEntity<Map<String, String>> editPassword(@RequestBody Map<String, String> request, @RequestHeader(name="Authorization") String token) {
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userService.getUserByUsername(username);

        adminService.editPassword(user.getId(), request.get("existingPassword"), request.get("newPassword"));

        //On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();
        response.put("updated", "Le mot de passe a bien été mis à jour.");

        logger.info(STR."l'administrateur' \{user.getUsername()} a changé de mot de passe avec succès.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /**
     * On crée la requête pour créer un modérateur
     * @param moderator nom d'utilisateur et mot de passe
     * @return ResponseEntity created
     */
    @PostMapping("/createModerator")
    public ResponseEntity<Map<String, String>> createModerator(@RequestBody Moderator moderator) {

        //On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();
        response.put("created", "Le modérateur a bien été créé.");

        moderatorService.createModerator(moderator);

        logger.info(STR."le modérateur \{moderator.getUsername()} a été créé.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /**
     * On crée la requête pour créer un agent de sécurité
     * @param security nom d'utilisateur et mot de passe
     * @return ResponseEntity created
     */
    @PostMapping("/createSecurity")
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

    /**
     * On crée la requête pour effacer un utilisateur
     * @param id identifiant de l'utilisateur à supprimer
     * @return ResponseEntity deleted
     */
    @DeleteMapping("users/{id}")
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
