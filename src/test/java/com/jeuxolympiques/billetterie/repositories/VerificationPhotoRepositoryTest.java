package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Moderator;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VerificationPhotoRepositoryTest {

    @Autowired
    private VerificationPhotoRepository verificationPhotoRepository;

    @Test
    void shouldGetAllVerificationPhotos() {
        List<VerificationPhoto> verificationPhotos = verificationPhotoRepository.findAll();

        assertEquals(6, verificationPhotos.size());
    }

    @Test
    void shouldGetVerificationPhotoById() {
        VerificationPhoto verificationPhoto = verificationPhotoRepository.findById("56245662-63e3-4c82-95e1-8e934c70ffdd").get();

        assertEquals(LocalDateTime.parse("2025-06-20T20:46:56.653694"), verificationPhoto.getVerificationDate());
        assertEquals("6d3b4384-5adf-442a-b18d-aeeb3dedcfcd", verificationPhoto.getModerator().getId());
        assertNull(verificationPhoto.getUrl());
    }

    @Test
    void shouldCreateVerificationPhoto() {
        VerificationPhoto verificationPhoto = new VerificationPhoto();
        Moderator moderator = new Moderator();
        verificationPhoto.setVerificationDate(LocalDateTime.parse("2025-06-20T20:46:56.653694"));
        verificationPhoto.setUrl("/uploads/08420325448.jpg");
        verificationPhoto.setModerator(moderator);

        VerificationPhoto savedVerificationPhoto = verificationPhotoRepository.save(verificationPhoto);

        assertNotNull(savedVerificationPhoto.getId());
        assertNotNull(savedVerificationPhoto.getModerator().getId());
        assertEquals(LocalDateTime.parse("2025-06-20T20:46:56.653694"), savedVerificationPhoto.getVerificationDate());
        assertEquals("/uploads/08420325448.jpg", savedVerificationPhoto.getUrl());
    }

    @Test
    void shouldUpdateVerificationPhoto() {
        VerificationPhoto verificationPhoto = verificationPhotoRepository.findById("b25147bf-fbb5-44aa-8a06-49076ed2fad3").get();
        verificationPhoto.setUrl("/uploads/08420325448dffd.jpg");
        VerificationPhoto savedVerificationPhoto = verificationPhotoRepository.save(verificationPhoto);

        assertEquals("/uploads/08420325448dffd.jpg", savedVerificationPhoto.getUrl());
    }

    @Test
    void shouldDeleteVerificationPhotoById() {
        verificationPhotoRepository.deleteById("b25147bf-fbb5-44aa-8a06-49076ed2fad3");
        Optional<VerificationPhoto> deletedVerificationPhoto = verificationPhotoRepository.findById("b25147bf-fbb5-44aa-8a06-49076ed2fad3");

        assertFalse(deletedVerificationPhoto.isPresent());
    }
}