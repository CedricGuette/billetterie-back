package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Security;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityController.class)
public class SecurityControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    SecurityService securityService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnMessageTicketIsValid() throws Exception {
        Security security1 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        Map<String, String> response = new HashMap<>();
        response.put("validated", "Le ticket de Dujardin Jean valable pour 2 places est validé !");

        when(jwtUtils.extractUsername("12345")).thenReturn("s@s");
        when(securityService.getSecurityByUsername("s@s")).thenReturn(security1);
        when(securityService.isThisTicketValid("123456789", "s@s")).thenReturn(response);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/security/123456789").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validated").value("Le ticket de Dujardin Jean valable pour 2 places est validé !"));
    }

}