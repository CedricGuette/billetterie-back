package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    EventService eventService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnAListOfEvent() throws Exception {

        Event event1 = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Event event2 = new Event("134c0aa8-0c22-4c94-8edf-f81c333db574","Finale 400 mètres 4 nages masculin","Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.",
                LocalDateTime.parse("2024-07-28T20:30:00.000000"), "/uploads/event/natation.jpeg", 17000, 17000, 35, 90, 110, null);

        when(eventService.getAllEvents()).thenReturn(List.of(event1,event2));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    void shouldReturnAnEventById() throws Exception {
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        when(eventService.getEventById("43729766-67b3-47d2-80f7-6ab87e0d4e5b")).thenReturn(event);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/event/43729766-67b3-47d2-80f7-6ab87e0d4e5b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Finale football masculin France - Espagne"));
    }

    @Test
    void shouldReturnCreatedEventMessage() throws Exception {
        String json = """
                {
                "name":"Finale football masculin France - Espagne",
                "description":"Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                "amount":"44260",
                "soloPrice":"50",
                "duoPrice":"90" ,
                "familyPrice":"160"
                }
                """;

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "event",
                "",
                String.valueOf(MediaType.APPLICATION_JSON),
                json.getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/event/post")
                        .file(jsonRequest)
                        .file(imageFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value("L'évènement Finale football masculin France - Espagne a bien été créé."));

    }

    @Test
    void shouldReturnUpdatedEventMessage() throws Exception {


        String json = """
                {
                "name":"Finale football masculin France - Espagne",
                "description":"Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                "amount":"44260",
                "soloPrice":"50",
                "duoPrice":"90" ,
                "familyPrice":"160"
                }
                """;

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "event",
                "",
                String.valueOf(MediaType.APPLICATION_JSON),
                json.getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );


        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/event/update/43729766-67b3-47d2-80f7-6ab87e0d4e5b");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(builder
                        .file(jsonRequest)
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value("L'évènement Finale football masculin France - Espagne a bien été mis à jour."));


    }

    @Test
    void shouldReturnDeletedEventMessage() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(delete("/api/event/delete/43729766-67b3-47d2-80f7-6ab87e0d4e5b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value("L'évènement avec l'id 43729766-67b3-47d2-80f7-6ab87e0d4e5b a bien été supprimé."));



    }

}