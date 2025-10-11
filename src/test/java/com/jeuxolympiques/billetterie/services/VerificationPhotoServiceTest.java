package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.repositories.VerificationPhotoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class VerificationPhotoServiceTest {

    @Mock
    VerificationPhotoRepository verificationPhotoRepository;

    @Mock
    ImageService imageService;

    @InjectMocks
    VerificationPhotoService verificationPhotoService;

    @Test
    void shouldReturnAVerificationPhotosList() {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1750506783231_image.jpg",LocalDateTime.parse("2025-06-20T19:20:24.714375"),null,null);
        VerificationPhoto verificationPhoto2 = new VerificationPhoto("56245662-63e3-4c82-95e1-8e934c70ffdd","src/ressources/static/uploads/1750511877071_398028867_10161234875279379_1180556476994756757_n.jpg",LocalDateTime.parse("2025-06-20T20:46:56.653694"),null,null);
        VerificationPhoto verificationPhoto3 = new VerificationPhoto("b1d9d623-e3ba-4b71-9aa4-fb5dde61569f",null,LocalDateTime.parse("2025-06-20T20:39:00.058063"),null, null);

        when(verificationPhotoRepository.findAll()).thenReturn(List.of(verificationPhoto1,verificationPhoto2, verificationPhoto3));

        List<VerificationPhoto> verificationPhotos = verificationPhotoService.getAllVerificationPhotos();

        assertThat(verificationPhotos).hasSize(2);
    }

    @Test
    void shouldReturnVerificationPhotoById() {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1750506783231_image.jpg",LocalDateTime.parse("2025-06-20T19:20:24.714375"),null,null);

        when(verificationPhotoRepository.findById("2191cfde-50eb-4d30-8eff-b978f07ebc81")).thenReturn(Optional.of(verificationPhoto1));

        VerificationPhoto verificationPhoto = verificationPhotoService.getVerificationPhotoById("2191cfde-50eb-4d30-8eff-b978f07ebc81");

        assertThat(verificationPhoto).isEqualTo(verificationPhoto1);
    }

    @Test
    void shouldUploadVerificationPhoto() throws IOException {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81",null,null,null,null);
        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"src/test/ressources/image.jpg\"}".getBytes()
        );
        customer1.setVerificationPhoto(verificationPhoto1);

        when(verificationPhotoRepository.save(verificationPhoto1)).thenReturn(verificationPhoto1);
        when(imageService.uploadImage(file, "uploads/verification/")).thenReturn("uploads/verification/12852151_image.jpg");

        verificationPhoto1.setUrl(null);
        VerificationPhoto updatedVerificationPhoto = verificationPhotoService.uploadVerificationPhoto(customer1, file);

        assertNotNull(updatedVerificationPhoto.getUrl());
        assertNull(updatedVerificationPhoto.getVerificationDate());
    }

    @Test
    void shouldDeleteVerificationPhoto() throws IOException {
        VerificationPhoto verificationPhoto = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1750506783231_image.jpg",null,null,null);
        Customer customer = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", verificationPhoto, null);
        when(verificationPhotoRepository.save(verificationPhoto)).thenReturn(verificationPhoto);

        VerificationPhoto deletedVerificationPhoto = verificationPhotoService.deleteVerificationPhoto(customer);

        assertThat(deletedVerificationPhoto.getUrl()).isNull();


    }

    @Test
    void shouldReturnCustomerFromVerificationPhotoId() {
        VerificationPhoto verificationPhoto1 = new VerificationPhoto("2191cfde-50eb-4d30-8eff-b978f07ebc81","src/ressources/static/uploads/1750506783231_image.jpg",LocalDateTime.parse("2025-06-20T19:20:24.714375"),null,null);
        Customer customer1 = new Customer("43729766-67b3-47d2-80f7-6ab87e0dd0b1", "gabriel@gmail.com", "1234", "ROLE_USER",
                LocalDateTime.parse("2025-06-20T16:51:01.867671"),"Gabriel", "Lapage", "0102030405",
                true, "8d771743-187c-4e59-bdad-364046cd0803", null, null);
        verificationPhoto1.setCustomer(customer1);

        when(verificationPhotoRepository.findById("2191cfde-50eb-4d30-8eff-b978f07ebc81")).thenReturn(Optional.of(verificationPhoto1));

        Customer customer = verificationPhotoService.getCustomerFromVerificationPhotoId("2191cfde-50eb-4d30-8eff-b978f07ebc81");

        assertThat(customer).isEqualTo(customer1);
    }
}