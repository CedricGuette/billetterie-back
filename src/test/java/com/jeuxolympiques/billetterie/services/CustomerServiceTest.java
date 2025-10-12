package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TicketService ticketService;

    @Mock
    private VerificationPhotoService verificationPhotoService;


    @InjectMocks
    private CustomerService customerService;


    @Test
    void shouldReturnAllCustomers() {

        VerificationPhoto verificationPhoto = new VerificationPhoto();

        Ticket ticket = new Ticket();
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", verificationPhoto, tickets);

        Customer customer2 = new Customer("7cea4f86-f0f4-4a6c-9504-156c4f7ece5f", "pablo@gmail.com", "abcd", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T19:19:47.113397"),"Pablo", "Picasso", "0102050607",
                true, "44b1621e-7cb6-4526-ba14-d334357ecb63", verificationPhoto, tickets);

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        List<Customer> customers = customerService.getAllCustomers();

        assertThat(customers).hasSize(2);
    }

    @Test
    void shouldReturnCustomerById() {
        VerificationPhoto verificationPhoto = new VerificationPhoto();

        Ticket ticket = new Ticket();
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", verificationPhoto, tickets);

        when(customerRepository.findById("43729766-67b3-47d2-80f7-6ab87e0dd0b1")).thenReturn(Optional.of(customer1));

        Customer customer = customerService.getCustomerById("43729766-67b3-47d2-80f7-6ab87e0dd0b1");

        assertThat(customer).isEqualTo(customer1);
    }

    @Test
    void shouldReturnCustomerByUsername() {
        VerificationPhoto verificationPhoto = new VerificationPhoto();

        Ticket ticket = new Ticket();
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", verificationPhoto, tickets);

        User user1 = new User("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"));

        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(user1);
        when(customerRepository.findById("43729766-67b3-47d2-80f7-6ab87e0dd0b1")).thenReturn(Optional.of(customer1));

        Customer customer = customerService.getCustomerByUsername("gabriel@gmail.com");

        assertThat(customer).isEqualTo(customer1);
    }

    @Test
    void shouldCreateCustomer() throws IOException {
        VerificationPhoto verificationPhoto = new VerificationPhoto();

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );

        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Ticket ticket = new Ticket(null,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null, event);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);

        when(customerRepository.save(customer1)).thenReturn(customer1);
        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(null);

        customer1.setVerificationPhoto(verificationPhoto);
        customer1.setTickets(tickets);

        Customer customer = customerService.createCustomer(customer1, event,passwordEncoder, file);

        assertThat(customer).isEqualTo(customer1);
    }

    @Test
    void shouldReturnAnException() throws IOException {
        VerificationPhoto verificationPhoto = new VerificationPhoto();

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );

        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne.","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Ticket ticket = new Ticket(null,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null, null);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);

        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(customer1);

        customer1.setVerificationPhoto(verificationPhoto);
        customer1.setTickets(tickets);
        
        String errorMessage = "";
        
        try {
            customerService.createCustomer(customer1, event,passwordEncoder, file);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage).isEqualTo("L'e-mail gabriel@gmail.com est déjà utilisé.");
    }

    @Test
    void shouldUpdateCustomer(){
        Customer customer = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);

        when(customerRepository.save(customer)).thenReturn(customer);

        customer.setFirstName("George");
        customer.setPhoneNumber("0607080910");

        Customer updatedCustomer = customerService.updateCustomer(customer);

        assertThat(updatedCustomer.getFirstName()).isEqualTo("George");
        assertThat(updatedCustomer.getPhoneNumber()).isEqualTo("0607080910");
        assertThat(updatedCustomer.getUsername()).isEqualTo("gabriel@gmail.com");
    }

    @Test
    void shouldAddNewTicketToCustomer() {
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Ticket ticket = new Ticket(null,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null, event);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, tickets);

        Event event2 = new Event("134c0aa8-0c22-4c94-8edf-f81c333db574","Finale 400 mètres 4 nages masculin","Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.",
                LocalDateTime.parse("2024-07-28T20:30:00.000000"), "/uploads/event/natation.jpeg", 17000, 17000, 35, 90, 110, null);

        Ticket ticket2 = new Ticket(null,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null, null);

        Ticket ticket3 = new Ticket("e78432f1-34b1-4da8-aa53-92e0850e4f2c", 4, false, false, null, null, null, null, null, null,
                LocalDateTime.now(), null, null, customer1, event2);

        when(ticketService.createTicket(ticket2, customer1, event2)).thenReturn(ticket3);
        when(customerRepository.save(customer1)).thenReturn(customer1);

        Customer ticketCreated = customerService.createNewTicket(ticket2, customer1, event2);

        assertThat(ticketCreated.getTickets().size()).isEqualTo(2);

    }
}