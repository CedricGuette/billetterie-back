package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        User user2 = new User("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", "m@m", "abcde", "ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:53.700600"));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldReturnUserById() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(userRepository.findById("019d5397-0a89-485f-95e2-00451582f1cd")).thenReturn(Optional.of(user1));

        User user = userService.getUserById("019d5397-0a89-485f-95e2-00451582f1cd");

        assertThat(user).isEqualTo(user1);
    }

    @Test
    void shouldReturnUserByUsername() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(userRepository.findByUsername("a@a")).thenReturn(user1);

        User user = userService.getUserByUsername("a@a");

        assertThat(user).isEqualTo(user1);
    }

    @Test
    void  shouldDeleteUser() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        userService.deleteUser(user1);

        verify(userRepository).delete(user1);
    }
}
