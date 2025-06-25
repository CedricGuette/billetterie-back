package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.exceptions.CreateUserUnauthorizedException;
import com.jeuxolympiques.billetterie.exceptions.DeleteUserUnauthorizedException;
import com.jeuxolympiques.billetterie.exceptions.EmailAlreadyUsedException;
import com.jeuxolympiques.billetterie.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    /*
     * Méthode pour créer un Admin
     */
    public Admin createAdmin (Admin admin) {

        // On vérifie qu'il n'y a pas déjà un administrateur
        if(Boolean.TRUE.equals(adminExist())) {
            throw new CreateUserUnauthorizedException("Il est impossible de créer un deuxième administrateur.");
        }

        // On vérifie que l'adresse mail n'est pas déjà utilisée
        if(userService.getUserByUsername(admin.getUsername()) != null) {
            new EmailAlreadyUsedException(STR."L'e-mail \{admin.getUsername()} est déjà utilisée.");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedDate(LocalDateTime.now());
        admin.setRole(String.valueOf(User.Role.ROLE_ADMIN));

        return adminRepository.save(admin);
    }

    /*
    * Méthode pour savoir si un admin existe déjà ou pas
    */
    public Boolean adminExist () {
        return adminRepository.count() > 0;
    }

    /*
    * Méthode pour supprimer un utilisateur
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
}
