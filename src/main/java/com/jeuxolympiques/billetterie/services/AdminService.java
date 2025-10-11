package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.exceptions.*;
import com.jeuxolympiques.billetterie.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Value("${ADMIN_USERNAME}")
    private String username;

    @Value("${ADMIN_PASSWORD}")
    private String password;

    private final AdminRepository adminRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    /**
     * Méthode pour créer un Admin
     * @param admin à enregistrer
     * @return l'administrateur sauvegardé en BDD
     */
    public Admin createAdmin (Admin admin) {

        // On vérifie qu'il n'y a pas déjà un administrateur
        if(Boolean.TRUE.equals(adminExist())) {
            throw new CreateUserUnauthorizedException("Il est impossible de créer un deuxième administrateur.");
        }

        // On vérifie que l'adresse mail n'est pas déjà utilisée
        if(userService.getUserByUsername(admin.getUsername()) != null) {
            throw new EmailAlreadyUsedException(STR."L'e-mail \{admin.getUsername()} est déjà utilisée.");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedDate(LocalDateTime.now());
        admin.setRole(String.valueOf(User.Role.ROLE_ADMIN));
        admin.setFirstLogin(true);

        return adminRepository.save(admin);
    }

    /**
     * Pour récupérer l'administrateur par son id (en utilisant un token)
     * @param id identifiant pour trouver l'administrateur dans la base de donnée
     * @return objet Admin
     */
    public Admin getAdminById(String id){
        Optional<Admin> admin = adminRepository.findById(id);

        if(admin.isPresent()){
            return admin.get();
        }

        throw new UserNotFoundException("L'utilisateur que vous cherchez n'a pas été trouvé.");
    }

    /**
     * Méthode pour changer le mot de passe de l'administrateur
     * @param id identifiant de l'admin (récupéré par token)
     * @param existingPassword le mot de passe actuel
     * @param newPassword le mot de passe par lequel on remplace l'ancien
     * @return l'administrateur une fois sauvegardé
     */
    public Admin editPassword(String id, String existingPassword, String newPassword){
        Admin admin = getAdminById(id);


        // On vérifie que les mots de passes correspondent
        if(passwordEncoder.matches(existingPassword, admin.getPassword())){
            admin.setPassword(passwordEncoder.encode(newPassword));

            // Si c'était le premier changement que l'on demande initialement à l'administrateur, on passe en faux
            if(admin.isFirstLogin()){
                admin.setFirstLogin(false);
            }

            return adminRepository.save(admin);
        }
        throw new EmailPasswordInvalidException("L'identifiant et le mot de passe ne correspondent pas.");
    }

    /**
     * Méthode pour savoir si un admin existe déjà ou pas
     * @return Booléen vrai si un administrateur existe déjà
     */
    public Boolean adminExist () {
        return adminRepository.count() > 0;
    }

    /**
     * Méthode pour renvoyer l'information si l'administrateur s'est déjà connecté ou pas
     * @param id identifiant de l'administrateur (récupéré par token)
     * @return Booléen vrai si adrministrateur connecté pour la première fois
     */
    public Boolean isFirstLogin (String id) {
        Admin admin = getAdminById(id);

        return admin.isFirstLogin();
    }

    /**
     * Méthode pour supprimer un utilisateur
     * @param user utilisateur à supprimer
     * @return un Map à renvoyé en réponse
     */
    public Map<String, String> deleteUser (User user){
        // Si l'utilisateur que l'on veut supprimer est un administrateur on renvoie une erreur
        if(user.getRole().equals(String.valueOf(User.Role.ROLE_ADMIN))){

            throw new DeleteUserUnauthorizedException("Vous ne pouvez pas supprimer l'administrateur.");
        }

        // Sinon on supprime
        userService.deleteUser(user);

        // On crée une réponse
        Map<String, String> response = new HashMap<>();
        response.put("deleted", "L'utilisateur a bien été supprimé.");

        return response;
    }

    /**
     * Méthode qui crée l'admin s'il n'existe pas déjà au lancement de l'application
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initiateEvent() {
        if(!adminExist()){
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);

            createAdmin(admin);
        }
    }
}
