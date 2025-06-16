package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    /*
     * Requête pour créer un Admin
     */
    public Admin createAdmin (Admin admin) {
        if(Boolean.TRUE.equals(adminExist())) {
            return null;
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedDate(LocalDateTime.now());
        admin.setRole("ROLE_ADMIN");
        return adminRepository.save(admin);
    }

    /*
    * Fonction pour savoir si un admin existe déjà ou pas
    */
    public Boolean adminExist () {
        return adminRepository.count() > 0;
    }
}
