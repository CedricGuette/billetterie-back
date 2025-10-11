package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.ModeratorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class ModeratorServiceTest {

    @Mock
    ModeratorRepository moderatorRepository;

    @Mock
    private UserService userService;

    @Mock
    private CustomerService customerService;

    @Mock
    private VerificationPhotoService verificationPhotoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    ModeratorService moderatorService;

    @Test
    void shouldCreateModerator() {
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m",passwordEncoder.encode("12345"),"ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Moderator moderator2 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(moderatorRepository.save(moderator1)).thenReturn(moderator1);
        Moderator savedModerator = moderatorService.createModerator(moderator2);

        assertThat(savedModerator).isEqualTo(moderator1);
    }

    @Test
    void shouldThrowExceptionForUsedEmail() {
        Moderator moderator2 = new Moderator(null,"m@m","12345",null, null);
        User user1 = new User("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(userService.getUserByUsername("m@m")).thenReturn(user1);

        String exceptionMessage = "";
        try {
            moderatorService.createModerator(moderator2);
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertThat(exceptionMessage).isEqualTo("L'e-mail m@m est déjà utilisé.");
    }

    @Test
    void shouldReturnModeratorById() {
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd")).thenReturn(Optional.of(moderator1));

        Moderator moderator = moderatorService.getModeratorById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd");

        assertThat(moderator).isEqualTo(moderator1);
    }

    @Test
    void shouldReturnModeratorByUsername() {
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        User user1 = new User("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(userService.getUserByUsername("m@m")).thenReturn(user1);
        when(moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd")).thenReturn(Optional.of(moderator1));

        Moderator moderator = moderatorService.getModeratorByUsername("m@m");

        assertThat(moderator).isEqualTo(moderator1);
    }

    @Test
    void shouldReturnAllVerificationPhotosNotValidated() {
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, null, null, null);

        Customer customer2 = new Customer("7cea4f86-f0f4-4a6c-9504-156c4f7ece5f", "pablo@gmail.com", "abcd", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T19:19:47.113397"),"Pablo", "Picasso", "0102050607",
                true, "44b1621e-7cb6-4526-ba14-d334357ecb63", null, null);

        Customer customer3 = new Customer("9a65095a-3edb-451c-90cf-20b5afac8b6f", "patoche@msn.com", "abcdef", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T20:46:50.771745"),"Patrick", "Petit", "0102050609",
                true, null, null, null);

        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1.jpg", null, customer1, null);
        VerificationPhoto verificationPhoto2 = new VerificationPhoto("dff0e290-ba63-436e-adf9-681f2fb84ec2","", LocalDateTime.parse("2025-06-20T19:46:44.098304"), customer2, moderator1);
        VerificationPhoto verificationPhoto3 = new VerificationPhoto("e78432f1-34b1-4da8-aa53-92e0850e4f2c","src/ressources/static/uploads/3.jpg", null, customer3, null);

        when(verificationPhotoService.getAllVerificationPhotos()).thenReturn(List.of(verificationPhoto1,verificationPhoto2,verificationPhoto3));

        List<VerificationPhoto> verificationPhotos = moderatorService.getAllVerificationPhotos();

        assertThat(verificationPhotos).hasSize(3);
    }

    @Test
    void shouldReturnMessageWhenVerificationPhotoIsValidated() throws IOException {
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        User user1 = new User("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","12345","ROLE_MODERATOR", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, null, null, null);
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1.jpg", null, customer1, null);

        when(verificationPhotoService.getVerificationPhotoById("2191cfde-50eb-4d30-8eff-b978f07ebc81")).thenReturn(verificationPhoto1);
        when(userService.getUserByUsername("m@m")).thenReturn(user1);
        when(moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd")).thenReturn(Optional.of(moderator1));

        Map<String, String> result = moderatorService.photoValidationById("2191cfde-50eb-4d30-8eff-b978f07ebc81", "m@m");
        Map<String, String> responseWitness = new HashMap<>();
        responseWitness.put("validated", "La compte a bien été validé, la photo a été supprimée.");

        assertThat(result).isEqualTo(responseWitness);

    }

}