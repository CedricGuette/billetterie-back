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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<String>> createModerator(@RequestBody Moderator moderator,@RequestHeader(name="Authorization") String token) {
        List<String> response = new ArrayList<>();
        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        if(user.getRole().equals("ROLE_ADMIN")){
            if(userRepository.findByUsername(moderator.getUsername()) != null) {
                response.add("Cet e-mail est déjà utilisé.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
            }
            moderatorService.createModerator(moderator);
            response.add("Le modérateur a bien été créé.");
            return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.add(NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
    }

    /*
    * On crée la requête pour créer un agent de sécurité
    */
    @PostMapping("/createSecurity")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<String>> createSecurity(@RequestBody Security security, @RequestHeader(name="Authorization") String token) {
        List<String> response = new ArrayList<>();
        // On récupère l'information du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        if(user.getRole().equals("ROLE_ADMIN")) {
            if(userRepository.findByUsername(security.getUsername()) != null) {
                response.add("Cet e-mail est déjà utilisé.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
            }
            Security securityCreated = securityService.createSecurity(security);
            response.add("L'agent de sécurité a bien été créé.");
            return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.add(NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
    }

    /*
     * On crée la requête pour créer effacer un utilisateur
     */
    @DeleteMapping("users/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<String>> deleteUserById (@PathVariable String id, @RequestHeader(name="Authorization") String token) {
        List<String> response = new ArrayList<>();
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        if(user.getRole().equals("ROLE_ADMIN")) {
            Optional<User> userToDelete = userRepository.findById(id);
            if(userToDelete.isPresent()) {

                if(userToDelete.get().getRole().equals("ROLE_ADMIN")){
                    response.add("Vous ne pouvez pas supprimer l'administrateur.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
                }

                userRepository.delete(userToDelete.get());
                response.add("L'utilisateur a bien été supprimé.");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(response);
            }
            response.add("Impossible de trouver l'utilisateur.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.add(NOT_AUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
    }
}
