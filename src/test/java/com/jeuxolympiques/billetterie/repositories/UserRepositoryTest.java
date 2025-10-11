package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldGetAllPersons() {

        List<User> users = userRepository.findAll();

        assertEquals(10, users.size());

        assertEquals("a@a", users.getFirst().getUsername());
    }

    @Test
    void shouldGetUserById() {
        User user = userRepository.findById("67c3557c-174d-4017-b8f6-d9ca5e6aaf71").get();

        assertEquals("fabien@gmail.com", user.getUsername());

        assertEquals(LocalDateTime.parse("2025-06-20T19:46:38.364679"), user.getCreatedDate());
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setUsername("moha@msn.com");
        user.setRole("ROLE_USER");
        user.setCreatedDate(LocalDateTime.parse("2025-06-21T01:13:01.251185"));
        user.setPassword("12345");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("moha@msn.com", savedUser.getUsername());
        assertEquals(LocalDateTime.parse("2025-06-21T01:13:01.251185"), savedUser.getCreatedDate());
        assertEquals("12345", savedUser.getPassword());
    }

    @Test
    void shouldUpdateUser() {
        User user = userRepository.findById("67c3557c-174d-4017-b8f6-d9ca5e6aaf71").get();

        user.setUsername("bonjour@gmail.com");
        User savedUser = userRepository.save(user);

        assertEquals("bonjour@gmail.com", savedUser.getUsername());
    }

    @Test
    void shouldDeleteUserById() {

        userRepository.deleteById("8f2ff1cd-8075-43a3-b504-a3fbfcb6b9e4");

        Optional<User> userDeleted = userRepository.findById("8f2ff1cd-8075-43a3-b504-a3fbfcb6b9e4");
        assertFalse(userDeleted.isPresent());
    }
}