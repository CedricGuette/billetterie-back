package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Moderator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ModeratorRepositoryTest {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Test
    void shouldGetAllModerators(){
        List<Moderator> moderators = moderatorRepository.findAll();

        assertEquals(1, moderators.size());
    }

    @Test
    void shouldGetModeratorById(){
        Moderator moderator = moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd").get();

        assertEquals("m@m", moderator.getUsername());
        assertEquals("ROLE_MODERATOR", moderator.getRole());
        assertEquals(LocalDateTime.parse("2025-06-20T16:49:53.700600"), moderator.getCreatedDate());
    }

    @Test
    void shouldCreateModerator(){
        Moderator moderator = new Moderator();
        moderator.setUsername("moderateguy@uol.com");
        moderator.setRole("ROLE_MODERATOR");
        moderator.setPassword("abcde");
        moderator.setCreatedDate(LocalDateTime.parse("2025-06-20T16:49:53.700600"));

        Moderator savedModerator = moderatorRepository.save(moderator);

        assertNotNull(savedModerator.getId());
        assertEquals("moderateguy@uol.com", savedModerator.getUsername());
        assertEquals("ROLE_MODERATOR", savedModerator.getRole());
        assertEquals("abcde", savedModerator.getPassword());
        assertEquals(LocalDateTime.parse("2025-06-20T16:49:53.700600"),savedModerator.getCreatedDate());

    }

    @Test
    void shouldUpdateModerator(){
        Moderator moderator = moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd").get();
        moderator.setUsername("momo@msn.com");
        Moderator savedModerator = moderatorRepository.save(moderator);

        assertEquals("momo@msn.com", savedModerator.getUsername());
    }

    @Test
    void shouldDeleteModeratorById(){
        moderatorRepository.deleteById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd");
        Optional<Moderator> deletedModerator = moderatorRepository.findById("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd");

        assertFalse(deletedModerator.isPresent());
    }

}