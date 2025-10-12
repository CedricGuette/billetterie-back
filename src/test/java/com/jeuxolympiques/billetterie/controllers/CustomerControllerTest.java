package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    EventService eventService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnCustomerObject() throws Exception {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,true,false,
                "2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W",
                "cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,
                "tickets/pdf/1750445549179_ticket.pdf", LocalDateTime.parse("2025-06-20T20:52:29.307152"),
                LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null, null);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, tickets);

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization","Bearer 12345");

        when(jwtUtils.extractUsername(tokenBearer.getFirst("Authorization").substring(7))).thenReturn("gabriel@gmail.com");
        when(customerService.getCustomerByUsername("gabriel@gmail.com")).thenReturn(customer1);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/customers").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("gabriel@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.firstName").value("Gabriel"))
                .andExpect(jsonPath("$.lastName").value("Lapage"))
                .andExpect(jsonPath("$.phoneNumber").value("0102030405"))
                .andExpect(jsonPath("$.profileIsValidate").value(true))
                .andExpect(jsonPath("$.customerKey").doesNotExist())
                .andExpect(jsonPath("$.tickets[0].ticketIsPayed").value(true));
    }

    @Test
    void shouldReturnAnotherTicketBoughtMessage() throws Exception {
        Event event1 = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Event event2 = new Event("134c0aa8-0c22-4c94-8edf-f81c333db574","Finale 400 mètres 4 nages masculin","Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.",
                LocalDateTime.parse("2024-07-28T20:30:00.000000"), "/uploads/event/natation.jpeg", 17000, 17000, 35, 90, 110, null);

        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,true,false,
                "2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W",
                "cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,
                "tickets/pdf/1750445549179_ticket.pdf", LocalDateTime.parse("2025-06-20T20:52:29.307152"),
                LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null, event1);

        Ticket ticket2 = new Ticket(null,1,null,null,
                null,null,null,
                null,null,
                null, null,
                null,null,null, null);
        String json = """
                {
                "howManyTickets":"1"
                }
                """;

        List<Ticket> tickets = new ArrayList<>();

        tickets.add(ticket1);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, tickets);

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        when(jwtUtils.extractUsername("12345")).thenReturn("gabriel@gmail.com");
        when(customerService.getCustomerByUsername("gabriel@gmail.com")).thenReturn(customer1);
        when(eventService.getEventById("134c0aa8-0c22-4c94-8edf-f81c333db574")).thenReturn(event2);


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/customers/buy/134c0aa8-0c22-4c94-8edf-f81c333db574").headers(tokenBearer).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value("Votre ticket a bien été créé."));

    }

}