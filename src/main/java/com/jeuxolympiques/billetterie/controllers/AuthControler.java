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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthControler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final AdminService adminService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private HttpHeadersCORS headersCORS = new HttpHeadersCORS();

    /*
     * Requête pour créer un compte et réserver sa place
     */
    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, String>> register(@RequestPart Customer customer, @RequestPart("photo") MultipartFile imageFile) throws IOException {

        // On crée l'objet à envoyer en réponse
        Map<String, String> response = new HashMap<>();

        //On vérifie que l'email n'est pas déjà en base de données
        if(userRepository.findByUsername(customer.getUsername()) != null) {

            response.put("error", "L'adresse e-mail est déjà utilisée.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
        }

        // Sinon on crée la réservation
        customerService.createCustomer(customer, passwordEncoder, imageFile);
        response.put("created", "La réservation a bien été créée. Veuillez patienter le temps qu'un modérateur valide votre identité.");
        return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
    }

    /*
     * Requête pour se connecter au service
     */
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Map<String, ?>> login(@RequestBody User user) {

        // On crée l'objet à envoyer en réponse
        Map<String, String> response = new HashMap<>();

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
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(authData);
            }

            // Sinon on renvoie une erreur
            response.put("error", "Adresse e-mail ou mot de passe invalide.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);

        } catch (AuthenticationException e) {

            // En cas d'exception on renvoie un message d'erreur et on log
            response.put("error", "Adresse e-mail ou mot de passe invalide.");
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body(response);
        }
    }

    @GetMapping("/level")
    @CrossOrigin(origins = "http://localhost:3000")
    @JsonView(Views.UserRole.class)
    public ResponseEntity<List<String>> getAuthLevel(@RequestHeader(name="Authorization") String token) {
        List<String> role = new ArrayList<>();
        if (token.isEmpty()){
            role.add("ROLE_UNKNOWN");
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(role);
        }
        String username = jwtUtils.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);
        if(user != null) {
            role.add(user.getRole());
            return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(role);
        }
        role.add("ROLE_UNKNOWN");
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body((role));
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
            response.put("error", "Cet e-mail est déjà utilisé.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
        }

        // On vérifie qu'il n'y a pas déjà d'Admin
        if(adminService.adminExist()) {
            response.put("error", "Impossible de créer un deuxième administrateur.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
        }

        // Si les conditions sont remplies on crée un admin
        adminService.createAdmin(admin);
        response.put("created", "L'administrateur a bien été créé.");
        return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
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
