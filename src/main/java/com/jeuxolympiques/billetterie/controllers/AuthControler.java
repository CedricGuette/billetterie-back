package com.jeuxolympiques.billetterie.controllers;

import com.fasterxml.jackson.annotation.JsonView;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.Views;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.services.AdminService;
import com.jeuxolympiques.billetterie.services.CustomerService;

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
public class AuthControler {

    private final UserRepository userRepository;

    private final CustomerService customerService;
    private final AdminService adminService;

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(AuthControler.class);

    // On met les réponses dans des variables
    private static final String EMAIL_ALREADY_USED = "Cet e-mail est déjà utilisé.";
    private static final String CUSTOMER_CREATED = "La réservation a bien été créée. Veuillez patienter le temps qu'un modérateur valide votre identité.";
    private static final String EMAIL_PASSWORD_INVALID = "Adresse e-mail ou mot de passe invalide.";
    private static final String SESSION_ERROR = "Votre session présente un problème veuillez vous reconnecter, merci.";
    private static final String ONLY_ONE_ADMIN = "Impossible de créer un deuxième administrateur.";
    private static final String CREATED_ADMIN = "L'administrateur a bien été créé.";

    private static final String ROLE_UNKNOWN = "ROLE_UNKNOWN";

    private static final String ERRORJSON = "error";
    private static final String ROLEJSON = "role";
    private static final String CREATEDJSON = "created";

    private static final String LOGIN_SUCCESS = "Quelqu'un s'est connecté avec succès.";
    private static final String LOGIN_FAIL = "Quelqu'un a éssayé de se connecter sans succès.";
    private static final String UNKNOWN_USER_CONNECTED = "Un utilisateur inconnu est connecté.";
    private static final String SESSION_ERROR_LOGGER = "Il y a une erreur de session detectée.";

    /*
     * Requête pour créer un compte et réserver sa place
     */
    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> register(@RequestPart Customer customer, @RequestPart("photo") MultipartFile imageFile) throws IOException {

        // On crée l'objet à envoyer en réponse
        Map<String, String> response = new HashMap<>();

        customer.setUsername(customer.getUsername().toLowerCase());

        //On vérifie que l'email n'est pas déjà en base de données
        if(userRepository.findByUsername(customer.getUsername()) != null) {

            response.put(ERRORJSON, EMAIL_ALREADY_USED);
            logger.error(EMAIL_ALREADY_USED);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }

        // Sinon on crée la réservation
        customerService.createCustomer(customer, passwordEncoder, imageFile);
        response.put(CREATEDJSON, CUSTOMER_CREATED);
        logger.info(CUSTOMER_CREATED);

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

        user.setUsername(user.getUsername().toLowerCase());
        // On crée l'objet à envoyer en réponse
        Map<String, Object> response = new HashMap<>();

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
                logger.info(LOGIN_SUCCESS);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .header(String.valueOf(headersCORS.headers()))
                        .body(authData);
            }

            // Sinon on renvoie une erreur
            response.put(ERRORJSON, EMAIL_PASSWORD_INVALID);
            logger.error(LOGIN_FAIL);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);

        } catch (AuthenticationException e) {

            // En cas d'exception on renvoie un message d'erreur et on log
            response.put(ERRORJSON, EMAIL_PASSWORD_INVALID);
            logger.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
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

        // S'il n'y a pas de token, on renvoie une réponse par défaut
        if (token.isEmpty()){
            response.put(ROLEJSON, ROLE_UNKNOWN);
            logger.info(UNKNOWN_USER_CONNECTED);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }

        // On vérifie l'information de rôle à partir du token
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        // Si l'utilisateur existe
        if(user != null) {
            response.put(ROLEJSON, user.getRole());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }

        // Si l'utilisateur n'est pas trouvé on renvoie une erreur
        response.put(ERRORJSON, SESSION_ERROR);
        logger.error(SESSION_ERROR_LOGGER);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .body((response));
    }

    /*
    * Requête pour créer un administrateur
    */
    @PostMapping("/createAdmin")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> createAdmin(@RequestBody Admin admin) {
        Map<String, String> response = new HashMap<>();

        // On vérifie que l'adresse mail n'est pas déjà utilisée
        if(userRepository.findByUsername(admin.getUsername()) != null) {
            response.put(ERRORJSON, EMAIL_ALREADY_USED);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }

        // On vérifie qu'il n'y a pas déjà d'Admin
        if(Boolean.TRUE.equals(adminService.adminExist())) {
            response.put(ERRORJSON, ONLY_ONE_ADMIN);
            logger.error(ONLY_ONE_ADMIN);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header(String.valueOf(headersCORS.headers()))
                    .body(response);
        }

        // Si les conditions sont remplies on crée un admin
        adminService.createAdmin(admin);
        response.put(CREATEDJSON, CREATED_ADMIN);
        logger.info(CREATED_ADMIN);

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
    public Boolean adminExist() {
        return adminService.adminExist();
    }
}
