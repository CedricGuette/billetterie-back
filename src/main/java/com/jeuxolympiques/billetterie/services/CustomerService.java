package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.exceptions.EmailAlreadyUsedException;
import com.jeuxolympiques.billetterie.exceptions.UserNotFoundException;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    // On importe les repositories utiles au traitement des données
    private final CustomerRepository customerRepository;

    private final UserService userService;
    private final VerificationPhotoService verificationPhotoService;
    private final TicketService ticketService;

    /*
     * Méthode pour récupérer sous forme de list<> l'ensemble des clients
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /*
     * Méthode pour récupérer un client en fonction de l'id demandé
     */
    public Customer getCustomerById (String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            return customer.get();
        }
        throw new UserNotFoundException("Le client que vous cherchez n'a pas été trouvé.");
    }

    /*
     * Méthode pour récupérer un client depuis son adresse e-mail
     */
    public Customer getCustomerByUsername(String username) {
        User user = userService.getUserByUsername(username);

        return getCustomerById(user.getId());
    }

    /*
    * Méthode pour mettre à jour un client dans la base de données
    */
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    /*
     * Méthode pour créer un nouveau client
     */
    public Customer createCustomer(Customer customer, PasswordEncoder passwordEncoder, MultipartFile imageFile) throws IOException {

        // On vérifie que l'adresse mail n'est pas déjà utilisée
        if(userService.getUserByUsername(customer.getUsername()) != null) {

            throw new EmailAlreadyUsedException(STR."L'e-mail \{customer.getUsername()} est déjà utilisé.");
        }

        // On appelle le service d'upload des services de VerificationPhoto
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setCreatedDate(LocalDateTime.now());
        customer.setRole(String.valueOf(User.Role.ROLE_USER));

        customer.setCustomerKey(null);
        customer.setProfileIsValidate(false);
        customer.setCustomerKey(null);

        // On impose les valeurs sur l'objet Ticket de la requête
        Ticket ticket = customer.getTickets().getFirst();
        ticketService.createTicket(ticket, customer);

        verificationPhotoService.uploadVerificationPhoto(customer, imageFile);

        return customerRepository.save(customer);
    }
}
