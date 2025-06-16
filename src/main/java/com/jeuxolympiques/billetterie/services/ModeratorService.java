package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.ModeratorRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import com.jeuxolympiques.billetterie.repositories.VerificationPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeratorService {
    // On importe les repositories nécessaire au traitement des données
    private final VerificationPhotoRepository verificationPhotoRepository;
    private final ModeratorRepository moderatorRepository;
    private final CustomerRepository customerRepository;
    private final VerificationPhotoService verificationPhotoService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /*
    * Requête pour créer un modérateur
    */
    public Moderator createModerator (Moderator moderator) {
        moderator.setPassword(passwordEncoder.encode(moderator.getPassword()));
        moderator.setCreatedDate(LocalDateTime.now());
        moderator.setRole("ROLE_MODERATOR");
        return moderatorRepository.save(moderator);
    }

    /*
    * Requête pour récupérer sous forme de List<> l'ensemble des photos de vérification
    */
    public List<VerificationPhoto> getAllVerificationPhoto() {
        List<VerificationPhoto> listOfVerificationPhoto = new ArrayList<>();
        List<VerificationPhoto> allVerificationPhotos;
        allVerificationPhotos = verificationPhotoRepository.findAll();
        for(int i = 0 ; i < allVerificationPhotos.size() ; i++ ){
            if(allVerificationPhotos.get(i).getUrl() != null){
                listOfVerificationPhoto.add(allVerificationPhotos.get(i));
            }
        }
        return listOfVerificationPhoto;
    }

    /*
     * Requête pour récupérer la photo de vérification par rapport à l'id demandée
     */
    public VerificationPhoto getVerificationPhotoById(String id) {
        Optional<VerificationPhoto> verificationPhoto = verificationPhotoRepository.findById(id);
            return verificationPhoto.orElse(null);
    }

    /*
     * Requête pour valider une photo, la supprimer du serveur, générer une clef pour le client et lui affecter
     */
    public Boolean photoValidationById(String id, String username) throws IOException {
        Optional<VerificationPhoto> verificationPhoto = verificationPhotoRepository.findById(id);
        if(verificationPhoto.isPresent()) {

            // On récupère l'information en base de données
            VerificationPhoto existingVerificationPhoto = verificationPhoto.get();

            // On récupère le client rattaché à cette photo de vérification
            Customer verifiedCustomer = existingVerificationPhoto.getCustomer();

            // Supprimer la photo du serveur
            verificationPhotoService.deleteVerificationPhoto(verifiedCustomer);

            // On supprime l'url de la photo de verification
            existingVerificationPhoto.setUrl(null);
            existingVerificationPhoto.setVerificationDate(LocalDateTime.now());

            User user = userRepository.findByUsername(username);
            Optional<Moderator> moderator = moderatorRepository.findById(user.getId());
            existingVerificationPhoto.setModerator(moderator.get());

            // Génération de la clef d'utilisateur
            UUID customerKey = UUID.randomUUID();

            // On passe le statut du client en vérifié et on lui affecte la clef générée
            verifiedCustomer.setProfileIsValidate(true);
            verifiedCustomer.setCustomerKey(customerKey.toString());
            customerRepository.save(verifiedCustomer);
            return true;
        }
        return false;
    }
}
