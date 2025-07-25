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
        if(adminRepository.count() > 0) {
            return null;
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        admin.setCreatedDate(LocalDateTime.now().format(formatter));
        admin.setRole("ROLE_ADMIN");
        return adminRepository.save(admin);
    }

}
