package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.*;
import com.jeuxolympiques.billetterie.repositories.SecurityRepository;
import com.jeuxolympiques.billetterie.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class SecurityServiceTest {

    @Mock
    SecurityRepository securityRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Mock
    TicketService ticketService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SecurityService securityService;

    @Test
    void shouldCreateSecurity() {
        Security security1 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s",passwordEncoder.encode("12345"),"ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Security security2 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(securityRepository.save(security1)).thenReturn(security1);
        Security savedSecurity = securityService.createSecurity(security2);

        assertThat(savedSecurity).isEqualTo(security1);
    }

    @Test
    void shouldThrowExceptionForUsedEmail() {
        Security security2 = new Security(null,"s@s","12345",null, null);
        User user1 = new User("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(userService.getUserByUsername("s@s")).thenReturn(user1);
        String exceptionMessage = "";
        try {
            securityService.createSecurity(security2);
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertThat(exceptionMessage).isEqualTo("L'e-mail s@s est déjà utilisé.");
    }

    @Test
    void shouldReturnSecurityById() {
        Security security1 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));

        when(securityRepository.findById("bc9ba1a3-7e30-4e62-9573-d6c150326be7")).thenReturn(Optional.of(security1));

        Security security = securityService.getSecurityById("bc9ba1a3-7e30-4e62-9573-d6c150326be7");

        assertThat(security).isEqualTo(security1);
    }

    @Test
    void shouldReturnSecurityByUsername() {
        User user1 = new User("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Security security1 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        when(userService.getUserByUsername("s@s")).thenReturn(user1);
        when(securityRepository.findById("bc9ba1a3-7e30-4e62-9573-d6c150326be7")).thenReturn(Optional.of(security1));

        Security security = securityService.getSecurityByUsername("s@s");

        assertThat(security).isEqualTo(security1);
    }

    @Test
    void shouldReturnAMessageIfTicketValidOrNot() throws NoSuchAlgorithmException {
        Security security1 = new Security("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        User user1 = new User("bc9ba1a3-7e30-4e62-9573-d6c150326be7","s@s","12345","ROLE_SECURITY", LocalDateTime.parse("2025-06-20T16:49:39.500601"));
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1, true,false,"2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W","cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,"tickets/pdf/1750445549179_ticket.pdf",LocalDateTime.parse("2025-06-20T20:52:29.307152"), LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null, event);
        Customer customer1 = new Customer("d64400c8-8054-4a77-9908-250c55036594","raph@ptdr.fr","$2a$10$M.iveOTFmzOXqKuymo9qJuoCM6tBiLORTclYgXQD2LZYho42zUaXS","ROLE_USER",LocalDateTime.parse("2025-06-20T20:51:54.840509"),"Raph","Aelle","0102050607",true,"57890899-cb3b-4fdd-96c7-fb17ab6bde20",null, null);
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("b25147bf-fbb5-44aa-8a06-49076ed2fad3",null,LocalDateTime.parse("2025-06-20T20:52:00.072999"),null,null);
        Moderator moderator1 = new Moderator("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd","m@m","$2a$10$cSZhT8TSMQhwlFlxrFX3qe76a9FtMfU5tMMUFT9Vsq2ZJlLWZAL6a","ROLE_MODERATOR",LocalDateTime.parse("2025-06-20T16:49:53.700600"));
        ticket1.setCustomer(customer1);
        List<Ticket> tickets = new ArrayList<>();
        tickets.addFirst(ticket1);
        verificationPhoto1.setModerator(moderator1);
        verificationPhoto1.setCustomer(customer1);
        customer1.setVerificationPhoto(verificationPhoto1);
        customer1.setTickets(tickets);

        Map<String, String> result = new HashMap<>();
        result.put("validated", "Le ticket de Aelle Raph valable pour 1 places pour l'évènement Finale football masculin France - Espagne est validé !");

        when(ticketService.getTicketById("317419e5-beb9-4e0e-b471-0bd551865034")).thenReturn(ticket1);
        when(userService.getUserByUsername("s@s")).thenReturn(user1);
        when(securityRepository.findById("bc9ba1a3-7e30-4e62-9573-d6c150326be7")).thenReturn(Optional.of(security1));

        Map<String, String> responseMessage = securityService.isThisTicketValid("317419e5-beb9-4e0e-b471-0bd551865034dda50a4cce7729d7205b334f7b0052cba8c240034700d1dee72c763e3902fc89", "s@s");

        assertThat(responseMessage).isEqualTo(result);


    }
}


