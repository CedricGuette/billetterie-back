package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomUserDetailServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldReturnUserDetailsSingleton() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(userService.getUserByUsername("a@a")).thenReturn(user1);

        UserDetails userSingletonWitness = new org.springframework.security.core.userdetails.User(user1.getUsername(), user1.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user1.getRole())));

        UserDetails userSingleton = customUserDetailService.loadUserByUsername("a@a");

        assertThat(userSingleton).isEqualTo(userSingletonWitness);
    }

    @Test
    void shouldReturnUserNotFoundException() {

        when(userService.getUserByUsername("a@a")).thenReturn(null);
        String errorMessage = "";

        try {
            customUserDetailService.loadUserByUsername("a@a");
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        assertThat(errorMessage).isEqualTo("Utilisateur non trouvé: a@a");
    }
}