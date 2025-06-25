package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
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
class CustomerServiceTest {

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

        Ticket ticket = new Ticket(null,1,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );

        when(customerRepository.save(customer1)).thenReturn(customer1);
        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(null);

        customer1.setVerificationPhoto(verificationPhoto);
        customer1.setTickets(tickets);

        Customer customer = customerService.createCustomer(customer1, passwordEncoder, file);

        assertThat(customer).isEqualTo(customer1);
    }

    @Test
    void shouldReturnAnException() throws IOException {
        VerificationPhoto verificationPhoto = new VerificationPhoto();

        Ticket ticket = new Ticket(null,1,4,null,null,null,
                null,null,null,null,null,null,
                null,null,null);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );

        when(userService.getUserByUsername("gabriel@gmail.com")).thenReturn(customer1);

        customer1.setVerificationPhoto(verificationPhoto);
        customer1.setTickets(tickets);
        
        String errorMessage = "";
        
        try {
            customerService.createCustomer(customer1, passwordEncoder, file);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        assertThat(errorMessage).isEqualTo("L'e-mail gabriel@gmail.com est déjà utilisé.");
    }
}