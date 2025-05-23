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
    public Integer isThisTicketValid(String qrCode, String username) throws NoSuchAlgorithmException {
        // On extrait les 36 premiers caractères qui correspondent à l'id du ticket
        String idFromQrCode = qrCode.substring(0, 36);
        String hashFromQrCode = qrCode.substring(36);

        // On récupère les informations en base de données
        Optional<Ticket> ticketToVerify = ticketRepository.findById(idFromQrCode);
        if(ticketToVerify.isPresent()) {
            Ticket ticketExist = ticketToVerify.get();
            Customer customerToVerify = ticketExist.getCustomer();
            String customerKey = customerToVerify.getCustomerKey();
            String sellingKey = ticketExist.getSellingKey();
            // On reproduit le hashage
            String keysHashed = HashService.toHash(customerKey) + HashService.toHash(sellingKey);
            keysHashed = HashService.toHash(keysHashed);
            // On compare les deux hash
            if(keysHashed.equals(hashFromQrCode)) {
                // On vérifie que le ticket n'a pas déjà été utilisé
                if(!ticketExist.getTicketIsUsed()){
                    // On met l'information à jour dans la base de données
                    ticketExist.setTicketIsUsed(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                    ticketExist.setTicketValidationDate(LocalDateTime.now().format(formatter));
                    User user = userRepository.findByUsername(username);
                    Security security = securityRepository.getById(user.getId());
                    ticketExist.setSecurity(security);
                    ticketRepository.save(ticketExist);
                    return 0;
                }
                // le ticket a déjà été utilisé
                return 1;
            }
            // le ticket n'est pas valide, les hash ne correspondent pas
            return 2;
        }
        // le ticket n'est pas valide l'identifiant est incorrect
        return 3;
    }
}
