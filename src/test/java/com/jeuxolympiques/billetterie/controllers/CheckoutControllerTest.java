package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.services.CheckoutService;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.CustomerService;
import com.jeuxolympiques.billetterie.services.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
public class CheckoutControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    CheckoutService checkoutService;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    TicketService ticketService;

    @MockitoBean
    CustomUserDetailService customUserDetailService;

    @MockitoBean
    JwtUtils jwtUtils;

    @Test
    void shouldReturnCheckoutKey() throws Exception {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,
                false,false,"2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",
                null,null,null,null,null,
                LocalDateTime.parse("2025-06-20T20:52:29.307152"), null,null,
                null, null);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, tickets);

        ticket1.setCustomer(customer1);

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        Map<String, String> response = new HashMap<>();
        response.put("checkoutSessionClientSecret","1234");

        when(jwtUtils.extractUsername("12345")).thenReturn("gabriel@gmail.com");
        when(customerService.getCustomerByUsername("gabriel@gmail.com")).thenReturn(customer1);
        when(ticketService.getTicketById("317419e5-beb9-4e0e-b471-0bd551865034")).thenReturn(ticket1);
        when(checkoutService.checkoutSessionStart(ticket1)).thenReturn(response);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(post("/api/stripe/checkout/317419e5-beb9-4e0e-b471-0bd551865034").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutSessionClientSecret").value("1234"));
    }

    @Test
    void shouldReturIfCheckoutIsPayed() throws Exception {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,
                false,false,"2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",
                null,null,null,null,null,
                LocalDateTime.parse("2025-06-20T20:52:29.307152"), null,null,
                null, null);

        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, tickets);

        ticket1.setCustomer(customer1);

        HttpHeaders tokenBearer = new HttpHeaders();
        tokenBearer.add("Authorization", "Bearer 12345");

        Map<String, String> response = new HashMap<>();
        response.put("checkoutStatus", "Le paiement a bien été effectué!");

        when(jwtUtils.extractUsername("12345")).thenReturn("gabriel@gmail.com");
        when(customerService.getCustomerByUsername("gabriel@gmail.com")).thenReturn(customer1);
        when(ticketService.getTicketById("317419e5-beb9-4e0e-b471-0bd551865034")).thenReturn(ticket1);
        when(checkoutService.isCheckoutPayed(ticket1)).thenReturn(response);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/stripe/checkout/validation/317419e5-beb9-4e0e-b471-0bd551865034").headers(tokenBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutStatus").value("Le paiement a bien été effectué!"));
    }

}