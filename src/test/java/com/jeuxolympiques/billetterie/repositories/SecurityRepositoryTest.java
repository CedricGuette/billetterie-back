package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SecurityRepositoryTest {

    @Autowired
    private SecurityRepository securityRepository;

    @Test
    void shouldGetAllSecurities(){
        List<Security> securities = securityRepository.findAll();

        assertEquals(2, securities.size());
    }

    @Test
    void shouldGetSecurityById(){
        Security security = securityRepository.findById("8f2ff1cd-8075-43a3-b504-a3fbfcb6b9e4").get();

        assertEquals("s@s", security.getUsername());
        assertEquals("ROLE_SECURITY", security.getRole());
        assertEquals(LocalDateTime.parse("2025-06-20T16:50:03.832100"), security.getCreatedDate());
    }

    @Test
    void shouldCreateSecurity(){
        Security security = new Security();
        security.setUsername("samuel@outlook.com");
        security.setPassword("12345");
        security.setRole("ROLE_SECURITY");
        security.setCreatedDate(LocalDateTime.parse("2025-06-20T16:50:03.832100"));

        Security savedSecurity = securityRepository.save(security);

        assertNotNull(savedSecurity.getId());
        assertEquals("samuel@outlook.com", savedSecurity.getUsername());
        assertEquals("12345", savedSecurity.getPassword());
        assertEquals("ROLE_SECURITY", savedSecurity.getRole());
        assertEquals(LocalDateTime.parse("2025-06-20T16:50:03.832100"), savedSecurity.getCreatedDate());

    }

    @Test
    void shouldUpdateSecurity(){
        Security security = securityRepository.findById("bc9ba1a3-7e30-4e62-9573-d6c150326be7").get();

        security.setUsername("patrick@hotmail.com");
        Security savedSecurity = securityRepository.save(security);

        assertEquals("patrick@hotmail.com", savedSecurity.getUsername());
    }

    @Test
    void shouldDeleteSecurityById(){
        securityRepository.deleteById("bc9ba1a3-7e30-4e62-9573-d6c150326be7");
        Optional<Security> deletedSecurity = securityRepository.findById("bc9ba1a3-7e30-4e62-9573-d6c150326be7");

        assertFalse(deletedSecurity.isPresent());
    }
}