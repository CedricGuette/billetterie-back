package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        List<User> allUsers;
        allUsers = userRepository.findAll();
        return allUsers;
    }
}
