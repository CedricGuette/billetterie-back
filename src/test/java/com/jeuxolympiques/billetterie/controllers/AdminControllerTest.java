package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.Security;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private ModeratorService moderatorService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnUsersList() throws Exception {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        User user2 = new User("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", "m@m", "abcde", "ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:53.700600"));

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnIfItsFirstLogin() throws Exception {
        User user = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        when(jwtUtils.extractUsername("12345")).thenReturn("a@a");
        when(userService.getUserByUsername("a@a")).thenReturn(user);
        when(adminService.isFirstLogin("019d5397-0a89-485f-95e2-00451582f1cd")).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/admin/firstLogin").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void shouldReturnChangedPasswordMessage() throws Exception {

        User user = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a",passwordEncoder.encode("12345"),"ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Admin admin = new Admin("019d5397-0a89-485f-95e2-00451582f1cd","a@a",passwordEncoder.encode("12345"),"ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"),true);
        String json = """
                {
                "existingPassword":"12345",
                "newPassword":"54321"
                }
                """;

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        when(jwtUtils.extractUsername("12345")).thenReturn("a@a");
        when(userService.getUserByUsername("a@a")).thenReturn(user);
        when(adminService.editPassword("019d5397-0a89-485f-95e2-00451582f1cd","12345","54321")).thenReturn(admin);


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(put("/api/admin/password/change").headers(tokenBearer).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value("Le mot de passe a bien été mis à jour."));
    }

    @Test
    void shouldReturnCreatedModeratorMessage() throws Exception {
        Moderator moderator1 = new Moderator(null, "m@m", "1234", null, null);
        Moderator moderator2 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", "m@m", "1234", "ROLE_MODERATOR", LocalDateTime.now());

        String json = """
                {
                "username":"m@m",
                "password":"1234"
                }
                """;

        when(moderatorService.createModerator(moderator1)).thenReturn(moderator2);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/admin/createModerator").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value("Le modérateur a bien été créé."));
    }

    @Test
    void shouldReturnCreatedSecurityMessage() throws Exception {
        Security security1 = new Security(null, "s@s", "1234", null, null);
        Security security2 = new Security("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", "s@s", "1234", "ROLE_SECURITY", LocalDateTime.now());
        String json = """
                {
                "username":"s@s",
                "password":"1234"
                }
                """;

        when(securityService.createSecurity(security1)).thenReturn(security2);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/admin/createSecurity").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value("L'agent de sécurité a bien été créé."));

    }

    @Test
    void shouldReturnDeletedUserByIdMessage() throws Exception {
        User user1 = new User("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", "s@s", "1234", "ROLE_SECURITY", LocalDateTime.now());

        Map<String, String> response = new HashMap<>();
        response.put("deleted", "L'utilisateur a bien été supprimé.");

        when(userService.getUserById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd")).thenReturn(user1);
        when(adminService.deleteUser(user1)).thenReturn(response);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(delete("/api/admin/users/6d3b4384-5adf-442a-b18d-aeeb3dedcfcd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value("L'utilisateur a bien été supprimé."));
    }
}