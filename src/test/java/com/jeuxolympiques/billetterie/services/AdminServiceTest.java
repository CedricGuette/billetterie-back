package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Admin;
import com.jeuxolympiques.billetterie.entities.User;
import com.jeuxolympiques.billetterie.repositories.AdminRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldReturnCreatedAdmin() {
        Admin admin1 = new Admin("019d5397-0a89-485f-95e2-00451582f1cd","a@a",passwordEncoder.encode("12345"),"ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Admin admin2 = new Admin("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(adminRepository.save(admin1)).thenReturn(admin1);

        Admin savedAdmin = adminService.createAdmin(admin2);

        assertThat(savedAdmin).isEqualTo(admin1);

    }

    @Test
    void shouldReturnFalseIfAdminDontExist() {
        when(adminRepository.count()).thenReturn(0L);

        assertFalse(adminService.adminExist());
    }

    @Test
    void shouldReturnTrueIfAdminExist() {
        when(adminRepository.count()).thenReturn(1L);

        assertTrue(adminService.adminExist());
    }

    @Test
    void shouldDeleteUser() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                userRepository.delete(user1);
                return null;
            }
        }).when(userService).deleteUser(user1);
        adminService.deleteUser(user1);

        verify(userRepository).delete(user1);
    }

    @Test
    void shouldNotDeleteAdminException() {
        User user1 = new User("019d5397-0a89-485f-95e2-00451582f1cd","a@a","12345","ROLE_ADMIN", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        String errorMessage = "";
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                userRepository.delete(user1);
                return null;
            }
        }).when(userService).deleteUser(user1);
        try {
            adminService.deleteUser(user1);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        assertThat(errorMessage).isEqualTo("Vous ne pouvez pas supprimer l'administrateur.");
    }

}