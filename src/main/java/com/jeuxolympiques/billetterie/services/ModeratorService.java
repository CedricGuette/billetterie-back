package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.exceptions.EmailAlreadyUsedException;
import com.jeuxolympiques.billetterie.exceptions.UserNotFoundException;
import com.jeuxolympiques.billetterie.repositories.ModeratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ModeratorService {
    // On importe les repositories nécessaire au traitement des données
    private final ModeratorRepository moderatorRepository;

    private final UserService userService;
    private final CustomerService customerService;
    private final VerificationPhotoService verificationPhotoService;

    private final PasswordEncoder passwordEncoder;


    /*
    * Méthode pour créer un modérateur
    */
    public Moderator createModerator (Moderator moderator) {

        // On vérifie que l'adresse mail n'est pas déjà utilisée
        if(userService.getUserByUsername(moderator.getUsername()) != null) {

            throw new EmailAlreadyUsedException(STR."L'e-mail \{moderator.getUsername()} est déjà utilisé.");
        }

        moderator.setPassword(passwordEncoder.encode(moderator.getPassword()));
        moderator.setCreatedDate(LocalDateTime.now());
        moderator.setRole(String.valueOf(User.Role.ROLE_MODERATOR));

        // On enregistre le modérateur en base de données
        return moderatorRepository.save(moderator);

    }

    /*
    * Méthode pour récupérer un modérateur depuis son id
    */
    public Moderator getModeratorById(String id) {
        Optional<Moderator> moderator = moderatorRepository.findById(id);
        if(moderator.isPresent()){
            return moderator.get();
        }
        throw new UserNotFoundException("Le modérateur que vous cherchez n'a pas été trouvé.");
    }

    /*
    * Méthode pour récupérer un modérateur depuis son adresse e-mail
    */
    public Moderator getModeratorByUsername(String username) {
        User user = userService.getUserByUsername(username);

        return this.getModeratorById(user.getId());
    }

    /*
    * Méthode pour récupérer sous forme de List<> l'ensemble des photos de vérification encore actives
    */
    public List<VerificationPhoto> getAllVerificationPhotos() {
        return verificationPhotoService.getAllVerificationPhotos();
    }

    /*
     * Méthode pour valider une photo, la supprimer du serveur, générer une clef pour le client et lui affecter
     */
    public Map<String, String> photoValidationById(String photoId, String username) throws IOException {
        VerificationPhoto verificationPhoto = verificationPhotoService.getVerificationPhotoById(photoId);

        // On récupère le client rattaché à cette photo de vérification
        Customer customer = verificationPhoto.getCustomer();

        // Supprimer la photo du serveur
        verificationPhotoService.deleteVerificationPhoto(customer);

        // On supprime l'url de la photo de verification
        verificationPhoto.setUrl(null);
        verificationPhoto.setVerificationDate(LocalDateTime.now());

        Moderator moderator = this.getModeratorByUsername(username);

        verificationPhoto.setModerator(moderator);

        // Génération de la clef d'utilisateur
        UUID customerKey = UUID.randomUUID();

        // On passe le statut du client en vérifié et on lui affecte la clef générée
        customer.setProfileIsValidate(true);
        customer.setCustomerKey(customerKey.toString());
        customerService.updateCustomer(customer);

        // On crée la réponse
        Map<String, String> response = new HashMap<>();
        response.put("validated", "La compte a bien été validé, la photo a été supprimée.");
        return response;
    }
}
