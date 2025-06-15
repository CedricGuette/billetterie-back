package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import com.jeuxolympiques.billetterie.services.SecurityService;
import com.jeuxolympiques.billetterie.services.UserService;
import lombok.RequiredArgsConstructor;
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
    private final CustomerService customerService;
    private final ModeratorService moderatorService;
    private final SecurityService securityService;
    private final JwtUtils jwtUtils;
    private HttpHeadersCORS headersCORS = new HttpHeadersCORS();

    private final String NOT_AUTHORIZED = "Vous n'êtes pas autorisé à faire cette action";

    /*
     * On crée la requête pour récupérer la liste des utilisateurs
     */
    @GetMapping("/users")
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.Admin.class)
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(userService.getAllUsers());
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
        if(user.getRole().equals("ROLE_ADMIN")){

            // On vérifie que l'adresse mail n'est pas déjà utilisée
            if(userRepository.findByUsername(moderator.getUsername()) != null) {
                response.put("error", "Cet e-mail est déjà utilisé.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
            }
            moderatorService.createModerator(moderator);
            response.put("created", "Le modérateur a bien été créé.");
            return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.put("error", NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
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
        if(user.getRole().equals("ROLE_ADMIN")) {
            if(userRepository.findByUsername(security.getUsername()) != null) {
                response.put("error", "Cet e-mail est déjà utilisé.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
            }
            securityService.createSecurity(security);
            response.put("created", "L'agent de sécurité a bien été créé.");
            return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.put("error", NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
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
        if(user.getRole().equals("ROLE_ADMIN")) {

            // On vérifie que l'utilisateur que l'on veut supprimer existe
            Optional<User> userToDelete = userRepository.findById(id);
            if(userToDelete.isPresent()) {

                // Si l'utilisateur que l'on veut supprimer est un administrateur on renvoie une erreur
                if(userToDelete.get().getRole().equals("ROLE_ADMIN")){
                    response.put("error", "Vous ne pouvez pas supprimer l'administrateur.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
                }

                // Sinon on supprime
                userRepository.delete(userToDelete.get());
                response.put("deleted", "L'utilisateur a bien été supprimé.");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
            }

            response.put("error", "Impossible de trouver l'utilisateur.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.put("error", NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
    }
}
