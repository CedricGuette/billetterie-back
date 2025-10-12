package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    AdminService adminService;

    @MockitoBean
    EventService eventService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    AuthenticationManager authenticationManager;

    @MockitoBean
    CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnCreatedCustomer() throws Exception {

        String json = """
                {
                "firstName": "Jean",
                "lastName": "Dujardin",
                "phoneNumber": "0102030405",
                "username": "jeandujardin@famous.com",
                "password": "1234",
                "tickets": [
                        {
                            "howManyTickets": 1
                        }
                    ]
                }
                """;

        String json2 = """
                {
                "id": "67207e92-dc13-4a29-8f41-d96c3e191b98"
                }
                """;

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "customer",
                "",
                String.valueOf(MediaType.APPLICATION_JSON),
                json.getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "photo",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );

        MockMultipartFile jsonRequest2 = new MockMultipartFile(
                "event",
                "",
                String.valueOf(MediaType.APPLICATION_JSON),
                json2.getBytes()
        );

        VerificationPhoto verificationPhoto = new VerificationPhoto(null, null, null, null, null);

        Event event = new Event("67207e92-dc13-4a29-8f41-d96c3e191b98","Finale football masculin France - Espagne.","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);
        Ticket ticket = new Ticket(null, 1,  null, null, null, null,
                null, null, null, null, null, null,
                null, null, null);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer = new Customer("f45be7f7-a93d-4328-b0eb-13f7795c5856", "jeandujardin@famous.com",
                "1234", "ROLE_USER", LocalDateTime.now(), "Jean", "Dujardin",
                "0102030405", false, null, null, null);
        customer.setTickets(tickets);
        customer.setVerificationPhoto(verificationPhoto);


        when(customerService.createCustomer(customer, event, passwordEncoder, imageFile)).thenReturn(customer);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/auth/register")
                        .file(jsonRequest)
                        .file(jsonRequest2)
                        .file(imageFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value("Bienvenue Jean, votre réservation a bien été créée. Veuillez patienter le temps qu'un modérateur valide votre identité."));
    }

    @Test
    void shouldReturnLoginResponse() throws Exception {
        User user1 = new User("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com",
                "$2a$10$j2bAoW6akvEoX7SUAhkEqulBZ5Rj9cO0Q8t9F0cZDXksn0bbBeR8G", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"));

        UserDetails user1Details = customUserDetailService.loadUserByUsername("gabriel@gmail.com");

        String json = """
                {
                "username": "gabriel@gmail.com",
                "password": "Aa*12345"
                }
                """;

        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);

        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtUtils.generateToken(user1.getUsername())).thenReturn("1234");
        when(customUserDetailService.loadUserByUsername("gabriel@gmail.com")).thenReturn(user1Details);

        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(user1);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("1234"))
                .andExpect(jsonPath("$.type").value("Bearer"));

    }

    @Test
    void shouldReturnUserLevel() throws Exception {
        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 1234");

        String token = "Bearer 1234";

        User user1 = new User("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com",
                "$2a$10$j2bAoW6akvEoX7SUAhkEqulBZ5Rj9cO0Q8t9F0cZDXksn0bbBeR8G", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"));

        when(jwtUtils.extractUsername(token.substring(7))).thenReturn("gabriel@gmail.com");
        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(user1);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/auth/level").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }
}