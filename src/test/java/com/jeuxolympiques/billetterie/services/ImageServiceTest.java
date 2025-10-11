package com.jeuxolympiques.billetterie.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Test
    void shouldUploadImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );

        String result = imageService.uploadImage(file,"");

        assertThat(result).contains("image.jpg");

    }

    @Test
    void shouldUpdateImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );

        String result = imageService.updateImage(file,"","previousImage.jpg");

        assertThat(result).contains("image.jpg");
    }
}