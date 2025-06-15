package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.SecurityRepository;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    // On importe les repositories utiles aux services

    private final TicketRepository ticketRepository;
    private final SecurityRepository securityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Requête pour créer un modérateur
     */
    public Security createSecurity (Security security) {
        security.setPassword(passwordEncoder.encode(security.getPassword()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        security.setCreatedDate(LocalDateTime.now().format(formatter));
        security.setRole("ROLE_SECURITY");
        return securityRepository.save(security);
    }

    /*
    * Cette méthode permet de vérifier la validité du QRcode présent sur le ticket
    */
    public Map<String, String> isThisTicketValid(String qrCode, String username) throws NoSuchAlgorithmException {
        Map<String, String> response = new HashMap<>();

        // Si le QR code renvoyé ne fait la bonne taille
        if(qrCode.length() < 100 || qrCode.length() > 100) {
            response.put("error", "L'identifiant ne correspond à aucun élément connu");
            return response;
        }
        // On extrait les 36 premiers caractères qui correspondent à l'id du ticket
        String idFromQrCode = qrCode.substring(0, 36);
        String hashFromQrCode = qrCode.substring(36);

        // On récupère les informations en base de données
        Optional<Ticket> ticketToVerify = ticketRepository.findById(idFromQrCode);

        // Si le ticket existe
        if(ticketToVerify.isPresent()) {

            // On récupère le ticket et le client qui correspondent au QR code
            Ticket ticketExist = ticketToVerify.get();
            Customer customerToVerify = ticketExist.getCustomer();

            // On récupère les clés pour les hasher et ensuite les comparer
            String customerKey = customerToVerify.getCustomerKey();
            String sellingKey = ticketExist.getSellingKey();

            // On reproduit le hashage
            String keysHashed = HashService.toHash(customerKey) + HashService.toHash(sellingKey);
            keysHashed = HashService.toHash(keysHashed);

            // On compare les deux hash
            if(keysHashed.equals(hashFromQrCode)) {

                // On vérifie que le ticket n'a pas déjà été utilisé
                if(!ticketExist.getTicketIsUsed()){

                    // Si le ticket est encore valable, on met l'information à jour dans la base de données
                    ticketExist.setTicketIsUsed(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                    ticketExist.setTicketValidationDate(LocalDateTime.now().format(formatter));

                    // On récupère les information de l'agent de sécurité depuis l'username
                    User user = userRepository.findByUsername(username);
                    Security security = securityRepository.getById(user.getId());

                    ticketExist.setSecurity(security);
                    ticketRepository.save(ticketExist);

                    // Et on renvoit une réponse
                    response.put("validated", "Le ticket de " + customerToVerify.getLastName() + " " + customerToVerify.getFirstName() + " valable pour " + ticketExist.getHowManyTickets() + " places est validé !");
                    return response;
                }
                // le ticket a déjà été utilisé
                response.put("error", "Le ticket a déjà été utilisé");
                return response;
            }
            // le ticket n'est pas valide, les hash ne correspondent pas
            response.put("error", "L'identifiant ne correspond pas avec le reste du code");
            return response;
        }
        // le ticket n'est pas valide l'identifiant est incorrect
        response.put("error", "L'identifiant ne correspond à aucun élément connu");
        return response;
    }
}
