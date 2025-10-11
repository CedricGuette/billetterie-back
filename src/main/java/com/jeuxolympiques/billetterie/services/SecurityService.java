package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.exceptions.EmailAlreadyUsedException;
import com.jeuxolympiques.billetterie.exceptions.TicketAlreadyUsedException;
import com.jeuxolympiques.billetterie.exceptions.UserNotFoundException;
import com.jeuxolympiques.billetterie.repositories.SecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final SecurityRepository securityRepository;

    private final UserService userService;
    private final TicketService ticketService;

    private final PasswordEncoder passwordEncoder;

    /**
     * Méthode pour créer un agent de sécurité
     * @param security Informations de l'agent de sécurité à créer
     * @return Agent de sécurité sauvegardé en base de données
     */
    public Security createSecurity (Security security) {

        // On vérifie que l'adresse e-mail n'est pas utilisée
        if(userService.getUserByUsername(security.getUsername()) != null) {

            throw new EmailAlreadyUsedException(STR."L'e-mail \{security.getUsername()} est déjà utilisé.");
        }

        security.setPassword(passwordEncoder.encode(security.getPassword()));
        security.setCreatedDate(LocalDateTime.now());
        security.setRole(String.valueOf(User.Role.ROLE_SECURITY));

        // On enregistre l'agent en base de données
        Security savedSecurity = securityRepository.save(security);

        return savedSecurity;
    }

    /**
     * Méthode pour récupérer un agent de sécurité depuis son identifiant
     * @param id Identifiant de l'agent de sécurité cherché
     * @return L'Agent de sécurité correspondant à l'identifiant
     */
    public Security getSecurityById(String id) {
        Optional<Security> security = securityRepository.findById(id);
        if(security.isPresent()){
            return security.get();
        }
        throw new UserNotFoundException("L'agent de sécurité que vous cherchez n'a pas été trouvé.");
    }

    /**
     * Méthode pour récupérer un agent de sécurité depuis son adresse e-mail
     * @param username Nom d'utilisateur de l'agent de sécurité cherché
     * @return L'agent de sécurité qui corresponde au nom d'utilisateur
     */
    public Security getSecurityByUsername(String username) {
        User user = userService.getUserByUsername(username);

        return this.getSecurityById(user.getId());
    }

    /**
     * Méthode permettant de vérifier la validité du QRcode présent sur le ticket
     * @param qrCode Chaine de caractère récupérée via le QR code du client
     * @param username Nom d'utilisateur de l'agent de sécurité qui valide le billet
     * @return Un message informant sur la validité du billet
     * @throws NoSuchAlgorithmException
     */
    public Map<String, String> isThisTicketValid(String qrCode, String username) throws NoSuchAlgorithmException {
        Map<String, String> response = new HashMap<>();

        // Si le QR code renvoyé ne fait la bonne taille
        if(qrCode.length() != 100) {
            throw new IllegalArgumentException("L'identifiant ne correspond à aucun élément connu");
        }

        // On extrait les 36 premiers caractères qui correspondent à l'id du ticket
        String idFromQrCode = qrCode.substring(0, 36);
        String hashFromQrCode = qrCode.substring(36);

        // On récupère les informations en base de données
        Ticket ticket = ticketService.getTicketById(idFromQrCode);

        // On récupère le ticket et le client qui correspondent au QR code
        Customer customer = ticket.getCustomer();

        // On récupère l'évènement
        Event event= ticket.getEvent();

        // On récupère les clés pour les hasher et ensuite les comparer
        String customerKey = customer.getCustomerKey();
        String sellingKey = ticket.getSellingKey();

        // On reproduit le hashage
        String keysHashed = HashService.toHash(customerKey) + HashService.toHash(sellingKey);
        keysHashed = HashService.toHash(keysHashed);

        // On compare les deux hash
        if(keysHashed.equals(hashFromQrCode)) {

            // On vérifie que le ticket n'a pas déjà été utilisé
            if(!ticket.getTicketIsUsed()){

                // Si le ticket est encore valable, on met l'information à jour dans la base de données
                ticket.setTicketIsUsed(true);
                ticket.setTicketValidationDate(LocalDateTime.now());

                // On récupère les informations de l'agent de sécurité depuis l'username

                Security security = this.getSecurityByUsername(username);

                ticket.setSecurity(security);
                ticketService.updateTicket(ticket);

                // Et on renvoie une réponse
                response.put("validated", STR."Le ticket de \{customer.getLastName()} \{customer.getFirstName()} valable pour \{ticket.getHowManyTickets()} places pour l'évènement \{event.getName()} est validé !");
                return response;
            }
            // le ticket a déjà été utilisé
            throw new TicketAlreadyUsedException("Le ticket a déjà été utilisé.");
        }
        // le ticket n'est pas valide, les hash ne correspondent pas
        throw new IllegalArgumentException("L'identifiant ne correspond à aucun élément connu");
    }
}
