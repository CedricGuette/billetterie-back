package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.exceptions.UserNotFoundException;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Méthode pour récupérer l'ensemble des utilisateurs sour une forme de liste
     * @return List<> de l'ensemble des utilisateurs
     */
    public List<User> getAllUsers(){
        List<User> allUsers;
        allUsers = userRepository.findAll();
        return allUsers;
    }

    /**
     * Méthode pour récupérer un utilisateur depuis son adresse e-mail
     * @param username E-mail de l'utilisateur cherché
     * @return Utilisateur correspondant à l'e-mail
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Méthode pour récupérer un utilisateur grâce à son id
     * @param id Identifiant de l'utilisateur cherché
     * @return Utilisateur correspondant à l'identifiant
     */
    public User getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        throw new UserNotFoundException("L'utilisateur que vous cherchez n'a pas été trouvé.");
    }

    /**
     * Méthode pour supprimer un utilisateur
     * @param user Utilisateur à supprimer
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
