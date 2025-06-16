package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    // On importe les repositories utiles au traitement des données
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final VerificationPhotoService verificationPhotoService;
    private final TicketService ticketService;

    /*
     * Requête pour récupérer sous forme de list<> l'ensemble des clients
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /*
     * Requête pour récupérer un client en fonction de l'id demandée
     */
    public Customer getCustomerById (String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);
    }

    /*
     * Requête pour créer un nouveau client
     */
    public Customer createCustomer(Customer customer, PasswordEncoder passwordEncoder, MultipartFile imageFile) throws IOException {
        // On appelle le service d'upload des services de VerificationPhoto
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setCreatedDate(LocalDateTime.now());
        customer.setCustomerKey(null);
        customer.setProfileIsValidate(false);
        customer.setRole("ROLE_USER");
        customer.setCustomerKey(null);

        // On impose les valeurs sur l'objet Ticket de la requête
        Ticket ticket = customer.getTickets().getLast();
        ticketService.createTicket(ticket, customer);
        verificationPhotoService.uploadVerificationPhoto(customer, imageFile);
        return customerRepository.save(customer);
    }

    public List<Ticket> listOfTicketsFromCustomersId(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.get().getTickets();
    }
}
