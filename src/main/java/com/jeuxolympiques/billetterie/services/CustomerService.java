package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Event;
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

    /**
     * Méthode pour récupérer sous forme de list<> l'ensemble des clients
     * @return List de tous les clients
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Méthode pour récupérer un client en fonction de l'id demandé
     * @param id identifiant du client
     * @return Le client correspondant à l'id
     */
    public Customer getCustomerById (String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            return customer.get();
        }
        throw new UserNotFoundException("Le client que vous cherchez n'a pas été trouvé.");
    }

    /**
     * Méthode pour récupérer un client depuis son adresse e-mail
     * @param username nom d'utilisateur du client
     * @return Le client correspondant au nom d'utilisateur
     */
    public Customer getCustomerByUsername(String username) {
        User user = userService.getUserByUsername(username);

        return getCustomerById(user.getId());
    }

    /**
     * Méthode pour mettre à jour un client dans la base de données
     * @param customer le client à mettre à jour
     */
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Méthode pour créer un nouveau client
     * @param customer Les informations du client à créer
     * @param event L'évènement avec lequel le client crée son compte
     * @param passwordEncoder Pour encrypter le mot de passe
     * @param imageFile L'image de vérification du client
     * @return l'objet Customer enregistré en base de données
     * @throws IOException
     */
    public Customer createCustomer(Customer customer, Event event, PasswordEncoder passwordEncoder, MultipartFile imageFile) throws IOException {

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
        ticketService.createTicket(ticket, customer, event);

        verificationPhotoService.uploadVerificationPhoto(customer, imageFile);

        return customerRepository.save(customer);
    }

    /**
     * Méthode pour acheter un nouveau ticket pour un client enregistré
     * @param ticket Billet à créer
     * @param customer Client auquel on ajoute le billet
     * @param event Évènement pour lequel crée le billet
     * @return Le client enregistré en base de données
     */
    public Customer createNewTicket(Ticket ticket, Customer customer, Event event) {

        // On laisse le service des ticket remplir le ticket
        Ticket ticketInDatabase = ticketService.createTicket(ticket ,customer, event);

        // On récupère la liste des tickets du client et on ajoute le nouveau ticket
        List<Ticket> tickets = customer.getTickets();
        tickets.add(ticketInDatabase);
        customer.setTickets(tickets);

        return customerRepository.save(customer);
    }
}
