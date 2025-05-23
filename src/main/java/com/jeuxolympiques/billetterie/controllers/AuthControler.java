package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.User;
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

    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> register(@RequestPart Customer customer, @RequestPart("photo") MultipartFile imageFile) throws IOException {
        if(userRepository.findByUsername(customer.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body("Email déjà utilisé");
        }
        customerService.createCustomer(customer, passwordEncoder, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body("La réservation a bien été créée");
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if(authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getUsername()));
                authData.put("type", "Bearer");
                return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body(authData);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(String.valueOf(headersCORS.headers())).body("Invalid username or password");
        }
    }

    @GetMapping("/level")
    @CrossOrigin(origins = "http://localhost:3000")
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

    @PostMapping("/createAdmin")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<String>> createAdmin(@RequestBody Admin admin) {
        List<String> response = new ArrayList<>();
        if(userRepository.findByUsername(admin.getUsername()) != null) {
            response.add("Cet e-mail est déjà utilisé.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(String.valueOf(headersCORS.headers())).body(response);
        }
        Admin adminCreated = adminService.createAdmin(admin);
        if(adminCreated == null) {
            response.add("Impossible de créer un deuxième administrateur.");
            return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
        }
        response.add("L'administrateur a bien été créé.");
        return ResponseEntity.status(HttpStatus.CREATED).header(String.valueOf(headersCORS.headers())).body(response);
    }
}
