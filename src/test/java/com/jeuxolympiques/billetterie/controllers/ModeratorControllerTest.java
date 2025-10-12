package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.ModeratorService;
import com.jeuxolympiques.billetterie.services.VerificationPhotoService;
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
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModeratorController.class)
public class ModeratorControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    ModeratorService moderatorService;

    @MockitoBean
    VerificationPhotoService verificationPhotoService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnVerificationPhotos() throws Exception {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81",
                "src/ressources/static/uploads/1750506783231_image.jpg", LocalDateTime.parse("2025-06-20T19:20:24.714375"),
                null,null);

        VerificationPhoto verificationPhoto2 = new VerificationPhoto("56245662-63e3-4c82-95e1-8e934c70ffdd",
                "src/ressources/static/uploads/1750511877071_398028867_10161234875279379_1180556476994756757_n.jpg",
                LocalDateTime.parse("2025-06-20T20:46:56.653694"),null,null);

        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR",
                LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        when(jwtUtils.extractUsername("12345")).thenReturn("m@m");
        when(moderatorService.getModeratorByUsername("m@m")).thenReturn(moderator1);
        when(moderatorService.getAllVerificationPhotos()).thenReturn(List.of(verificationPhoto1, verificationPhoto2));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/moderators").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].url")
                        .value("src/ressources/static/uploads/1750506783231_image.jpg"))
                .andExpect(jsonPath("$.[1].url")
                        .value("src/ressources/static/uploads/1750511877071_398028867_10161234875279379_1180556476994756757_n.jpg"));
    }

    @Test
    void shouldReturnConfirmationMessageWhenDeletedVerificationPhoto() throws Exception {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81",
                "src/ressources/static/uploads/1750506783231_image.jpg", null,
                null,null);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                false, null, verificationPhoto1, null);

        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR",
                LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        Map<String, String> response = new HashMap<>();
        response.put("validated", "La compte a bien été validé, la photo a été supprimée.");

        when(jwtUtils.extractUsername("12345")).thenReturn("m@m");
        when(verificationPhotoService.getCustomerFromVerificationPhotoId("2191cfde-50eb-4d30-8eff-b978f07ebc81")).thenReturn(customer1);
        when(moderatorService.getModeratorByUsername("m@m")).thenReturn(moderator1);
        when(moderatorService.photoValidationById("2191cfde-50eb-4d30-8eff-b978f07ebc81", "m@m")).thenReturn(response);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(patch("/api/moderators/2191cfde-50eb-4d30-8eff-b978f07ebc81").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validated").value("La compte a bien été validé, la photo a été supprimée."));
    }

}