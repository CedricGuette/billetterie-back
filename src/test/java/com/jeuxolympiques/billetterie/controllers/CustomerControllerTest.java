package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.JwtUtils;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.services.CustomUserDetailService;
import com.jeuxolympiques.billetterie.services.CustomerService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnCustomerObject() throws Exception {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,1,true,false,
                "2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W",
                "cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,
                "tickets/pdf/1750445549179_ticket.pdf", LocalDateTime.parse("2025-06-20T20:52:29.307152"),
                LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null);

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

}