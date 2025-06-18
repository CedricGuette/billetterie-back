package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
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
public class AdminControler {

    private final UserRepository userRepository;

    private final UserService userService;
    private final ModeratorService moderatorService;
    private final SecurityService securityService;

    private final JwtUtils jwtUtils;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(AdminControler.class);

    // On met les réponses dans des variables
    private static final String NOT_AUTHORIZED = "Vous n'êtes pas autorisé à faire cette action.";
    private static final String EMAIL_ALREADY_USED = "Cet e-mail est déjà utilisé.";
    private static final String USER_NOT_FOUND = "Impossible de trouver l'utilisateur.";
    private static final String MODERATOR_CREATED = "Le modérateur a bien été créé.";
    private static final String SECURITY_CREATED = "L'agent de sécurité a bien été créé.";
    private static final String USER_DELETED = "L'utilisateur a bien été supprimé.";
    private static final String DELETE_ADMIN = "Vous ne pouvez pas supprimer l'administrateur.";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String ERRORJSON = "error";
    private static final String DELETEDJSON = "deleted";
    private static final String CREATEDJSON = "created";

    private static final String GET_USERS_UNAUTHORIZED = "Quelqu'un a éssayé d'obtenir la liste des utilisateurs sans la permission.";
    private static final String CREATE_MODERATOR_UNAUTHORIZED = "Quelqu'un a éssayé de créer un modérateur sans la permission.";
    private static final String CREATE_SECURITY_UNAUTHORIZED = "Quelqu'un a éssayé de créer un agent de sécurité sans la permission.";
    private static final String DELETE_USER_UNAUTHORIZED = "Quelqu'un a éssayé de supprimer un utilisateur sans la permission.";
    private static final String DELETE_ADMIN_LOGGER = "Quelqu'un a éssayé de supprimer l'administrateur.";


    /*
     * On crée la requête pour récupérer la liste des utilisateurs
     */
    @GetMapping("/users")
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.Admin.class)
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info(GET_USERS_UNAUTHORIZED);

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
    public ResponseEntity<Map<String, String>> createModerator(@RequestBody Moderator moderator,@RequestHeader(name="Authorization") String token) {

        //On crée la varaible qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On vérifie le rôle de l'utilisateur
        if(user.getRole().equals(ROLE_ADMIN)){

            // On vérifie que l'adresse mail n'est pas déjà utilisée
            if(userRepository.findByUsername(moderator.getUsername()) != null) {
                response.put(ERRORJSON, EMAIL_ALREADY_USED);
                logger.error(EMAIL_ALREADY_USED);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(response);
            }
            moderatorService.createModerator(moderator);
            response.put(CREATEDJSON, MODERATOR_CREATED);
            logger.info(MODERATOR_CREATED);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }
        response.put(ERRORJSON, NOT_AUTHORIZED);
        logger.error(CREATE_MODERATOR_UNAUTHORIZED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
    * On crée la requête pour créer un agent de sécurité
    */
    @PostMapping("/createSecurity")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> createSecurity(@RequestBody Security security, @RequestHeader(name="Authorization") String token) {

        // On crée la variable qui va accueillir la réponse
        Map<String, String> response = new HashMap<>();

        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On vérifie le rôle de l'utilisateur
        if(user.getRole().equals(ROLE_ADMIN)) {
            if(userRepository.findByUsername(security.getUsername()) != null) {
                response.put(ERRORJSON, EMAIL_ALREADY_USED);
                logger.warn(EMAIL_ALREADY_USED);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(response);
            }
            securityService.createSecurity(security);
            response.put(CREATEDJSON, SECURITY_CREATED);
            logger.info(SECURITY_CREATED);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }
        response.put(ERRORJSON, NOT_AUTHORIZED);
        logger.error(CREATE_SECURITY_UNAUTHORIZED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
     * On crée la requête pour effacer un utilisateur
     */
    @DeleteMapping("users/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> deleteUserById (@PathVariable String id, @RequestHeader(name="Authorization") String token) {
        // On crée la variable qui va recevoir la réponse
        Map<String, String> response = new HashMap<>();

        // On vérifie le token récupéré pour extraire l'utilisateur
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // On vérifie le rôle de l'utilisateur
        if(user.getRole().equals(ROLE_ADMIN)) {

            // On vérifie que l'utilisateur que l'on veut supprimer existe
            Optional<User> userToDelete = userRepository.findById(id);
            if(userToDelete.isPresent()) {

                // Si l'utilisateur que l'on veut supprimer est un administrateur on renvoie une erreur
                if(userToDelete.get().getRole().equals(ROLE_ADMIN)){
                    response.put(ERRORJSON, DELETE_ADMIN);
                    logger.error(DELETE_ADMIN_LOGGER);

                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .header(String.valueOf(headersCORS.headers()))
                            .body(response);
                }

                // Sinon on supprime
                userRepository.delete(userToDelete.get());
                response.put(DELETEDJSON, USER_DELETED);
                logger.info(USER_DELETED);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(response);
            }

            response.put(ERRORJSON, USER_NOT_FOUND);
            logger.error(USER_NOT_FOUND);

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }
        response.put(ERRORJSON, NOT_AUTHORIZED);
        logger.error(DELETE_USER_UNAUTHORIZED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }
}
