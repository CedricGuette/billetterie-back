package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.exceptions.EmailPasswordInvalidException;
import com.jeuxolympiques.billetterie.services.AdminService;
import com.jeuxolympiques.billetterie.services.CustomerService;

import com.jeuxolympiques.billetterie.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final CustomerService customerService;
    private final AdminService adminService;

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /*
     * Requête pour créer un compte et réserver sa place
     */
    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> register(@RequestPart Customer customer, @RequestPart("photo") MultipartFile imageFile) throws IOException {

        // On force l'adresse mail en minuscule
        customer.setUsername(customer.getUsername().toLowerCase());

        // On crée l'objet à envoyer en réponse
        Map<String, String> response = customerService.createCustomer(customer, passwordEncoder, imageFile);

        logger.info(STR."La réservation de \{customer.getUsername()} a bien été créée.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
     * Requête pour se connecter au service
     */
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {

        // On force l'adresse mail en minuscule
        user.setUsername(user.getUsername().toLowerCase());

        try {
            // On récupère les information d'authentification
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            // Si les informations sont cohérentes
            if(authentication.isAuthenticated()) {

                // On crée un token d'authentification
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getUsername()));
                authData.put("type", "Bearer");

                // On retourne le jeton
                logger.info(STR."L'utilisateur \{user.getUsername()} s'est connecté avec succès.");

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(authData);
            }
            // Sinon on renvoie une erreur
            throw new EmailPasswordInvalidException("Adresse e-mail ou mot de passe invalide.");

        } catch (AuthenticationException e) {
            throw new EmailPasswordInvalidException("Adresse e-mail ou mot de passe invalide.");
        }
    }

    /*
    * Requête pour récupérer le niveau d'accès d'un utilisateur
    */
    @GetMapping("/level")
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.UserRole.class)
    public ResponseEntity<Map<String, String>> getAuthLevel(@RequestHeader(name="Authorization") String token) {

        // On crée la réponse à renvoyer
        Map<String, String> response = new HashMap<>();
        String ROLEJSON = "role";

        // S'il n'y a pas de token, on renvoie une réponse par défaut
        if (token.isEmpty()){
            response.put(ROLEJSON, "ROLE_UNKNOWN");
            logger.info("Un utilisateur inconnu est connecté.");
        } else {
            // On vérifie l'information de rôle à partir du token
            String username = jwtUtils.extractUsername(token.substring(7));
            User user = userService.getUserByUsername(username);
            response.put(ROLEJSON, user.getRole());
            logger.info(STR."L'utilisateur \{user.getUsername()} est connecté avec le rôle \{user.getRole()}.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
    * Requête pour créer un administrateur
    */
    @PostMapping("/createAdmin")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> createAdmin(@RequestBody Admin admin) {

        // Si les conditions sont remplies on crée un admin
        Map<String, String> response = adminService.createAdmin(admin);
        logger.info(STR."L'administrateur \{admin.getUsername()} a bien été créé.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(headersCORS.headers()))
                .body(response);
    }

    /*
    * Requête pour vérifier si un admin existe déjà ou pas pour afficher l'option de création
    */
    @GetMapping("/doAdminExist")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Boolean> adminExist() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(headersCORS.headers()))
                .body(adminService.adminExist());
    }
}
