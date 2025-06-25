package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.services.AdminService;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.UserService;
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
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnCreatedCustomer() throws Exception {

        String json = """
                {
                "id": "43729766-67b3-47d2-80f7-6ab87e0dd0b1",
                "firstName": "Jean",
                "lastName": "Dujardin",
                "phoneNumber": "0102030405",
                "username": "jeandujardin@famous.com",
                "password": "1234",
                "tickets": [
                        {
                            "eventCode": "1",
                            "howManyTickets": 1
                        }
                    ]
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

        VerificationPhoto verificationPhoto = new VerificationPhoto(null, null, null, null, null);

        Ticket ticket = new Ticket(null, 1, 1, null, null, null, null,
                null, null, null, null, null, null,
                null, null);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer = new Customer("f45be7f7-a93d-4328-b0eb-13f7795c5856", "jeandujardin@famous.com",
                "1234", "ROLE_USER", LocalDateTime.now(), "Jean", "Dujardin",
                "0102030405", false, null, null, null);
        customer.setTickets(tickets);
        customer.setVerificationPhoto(verificationPhoto);

        when(customerService.createCustomer(customer, passwordEncoder, imageFile)).thenReturn(customer);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/auth/register")
                        .file(jsonRequest)
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

    @Test
    void shouldReturnThatCreatedAnAdmin() throws Exception {
        String json = """
                {
                "username": "a@a",
                "pasword": "1234"
                }
                """;
        Admin admin1 = new Admin("019d5397-0a89-485f-95e2-00451582f1cd","a@a",passwordEncoder.encode("1234"),"ROLE_ADMIN", LocalDateTime.now());
        Admin adminRequest = new Admin(null, "a@a", "1234", null, null);
        when(adminService.createAdmin(adminRequest)).thenReturn(admin1);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/auth/createAdmin").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.created").value("L'administrateur a bien été créé."));
    }

    @Test
    void shouldReturnIfAdminAlreadyExists() throws Exception {

        when(adminService.adminExist()).thenReturn(false);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/auth/doAdminExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}