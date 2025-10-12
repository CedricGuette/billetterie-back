package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void shouldGetAllAdmin() {
        List<Admin> admins = adminRepository.findAll();

        assertEquals(1, admins.size());
    }

    @Test
    void shouldGetAdminById() {
        Admin admin = adminRepository.findById("019d5397-0a89-485f-95e2-00451582f1cd").get();

        assertEquals("a@a", admin.getUsername());
        assertEquals(LocalDateTime.parse("2025-06-20T16:49:39.500601"), admin.getCreatedDate());
        assertEquals("ROLE_ADMIN", admin.getRole());

    }

    @Test
    void shouldSaveAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin@bober.fr");
        admin.setPassword("12345");
        admin.setRole("ROLE_ADMIN");
        admin.setCreatedDate(LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        Admin savedAdmin = adminRepository.save(admin);

        assertNotNull(savedAdmin.getId());
        assertEquals("admin@bober.fr", savedAdmin.getUsername());
        assertEquals("12345", savedAdmin.getPassword());
        assertEquals("ROLE_ADMIN", savedAdmin.getRole());
        assertEquals(LocalDateTime.parse("2025-06-20T16:49:39.500601"), savedAdmin.getCreatedDate());

    }

    @Test
    void shouldUpdateAdmin(){
        Admin admin = adminRepository.findById("019d5397-0a89-485f-95e2-00451582f1cd").get();

        admin.setUsername("admin@yahoo.fr");
        Admin savedAdmin = adminRepository.save(admin);

        assertEquals("admin@yahoo.fr", savedAdmin.getUsername());
    }

    @Test
    void shouldDeleteAdminById(){

        adminRepository.deleteById("019d5397-0a89-485f-95e2-00451582f1cd");

        Optional<Admin> deletedAdmin = adminRepository.findById("019d5397-0a89-485f-95e2-00451582f1cd");

        assertFalse(deletedAdmin.isPresent());
    }
}