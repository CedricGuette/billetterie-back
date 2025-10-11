package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldGetAllCustomers(){
        List<Customer> customers = customerRepository.findAll();

        assertEquals(6, customers.size());
    }

    @Test
    void shouldFindCustomerById(){
        Customer customer = customerRepository.findById("67c3557c-174d-4017-b8f6-d9ca5e6aaf71").get();

        assertEquals(LocalDateTime.parse("2025-06-20T19:46:38.364679"), customer.getCreatedDate());
        assertEquals("ROLE_USER", customer.getRole());
        assertEquals("fabien@gmail.com", customer.getUsername());
        assertEquals("Fabien", customer.getFirstName());
        assertEquals("0102050607", customer.getPhoneNumber());
        assertEquals(true, customer.getProfileIsValidate());
    }

    @Test
    void shouldCreateCustomer(){
        Customer customer = new Customer();

        Ticket ticket = new Ticket();
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        VerificationPhoto verificationPhoto = new VerificationPhoto();

        customer.setUsername("gregory@aol.fr");
        customer.setPassword("123456");
        customer.setRole("ROLE_USER");
        customer.setFirstName("Gregory");
        customer.setLastName("Lemarchand");
        customer.setPhoneNumber("0621222324");
        customer.setTickets(tickets);
        customer.setVerificationPhoto(verificationPhoto);

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertNotNull(savedCustomer.getTickets().getFirst().getId());
        assertNotNull(savedCustomer.getVerificationPhoto().getId());
        assertEquals("gregory@aol.fr", savedCustomer.getUsername());
        assertEquals("123456", savedCustomer.getPassword());
        assertEquals("ROLE_USER", savedCustomer.getRole());
        assertEquals("Gregory", savedCustomer.getFirstName());
        assertEquals("Lemarchand", savedCustomer.getLastName());
        assertEquals("0621222324", savedCustomer.getPhoneNumber());
        assertEquals(tickets, savedCustomer.getTickets());
    }

    @Test
    void shouldUpdateCustomer(){
        Customer customer = customerRepository.findById("7cea4f86-f0f4-4a6c-9504-156c4f7ece5f").get();
        customer.setUsername("jojo@msn.com");
        Customer savedUser = customerRepository.save(customer);

        assertEquals("jojo@msn.com", savedUser.getUsername());
    }

    @Test
    void shouldDeleteByIdCustomer() {
        customerRepository.deleteById("9a65095a-3edb-451c-90cf-20b5afac8b6f");
        Optional<Customer> deletedCustomer = customerRepository.findById("9a65095a-3edb-451c-90cf-20b5afac8b6f");

        assertFalse(deletedCustomer.isPresent());
    }

}