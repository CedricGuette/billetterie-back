package com.jeuxolympiques.billetterie.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HashServiceTest {


    @Test
    void shouldTransformStringIntoHashHex() throws NoSuchAlgorithmException {
        String valueToTransform = "12345";

        byte[] bytes = HashService.getHashHexadecimal(valueToTransform);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] bytesResutlt = messageDigest.digest(valueToTransform.getBytes(StandardCharsets.UTF_8));

        assertThat(bytes).isEqualTo(bytesResutlt);
    }

    @Test
    void shouldTransformHashHexToHashString() throws NoSuchAlgorithmException {
        String valueToTransform = "12345";

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytesResutlt = messageDigest.digest(valueToTransform.getBytes(StandardCharsets.UTF_8));
        String hashResult = HashService.fromHextoString(bytesResutlt);

        BigInteger number = new BigInteger(1, bytesResutlt);

        StringBuilder stringFromHex = new StringBuilder(number.toString(16));
        while (stringFromHex.length() < 64) {
            stringFromHex.insert(0,0);
        }

        String fonctionResult = stringFromHex.toString();

        String allFunction = HashService.toHash(valueToTransform);

        assertThat(fonctionResult).isEqualTo(hashResult);
        assertThat(allFunction).isEqualTo(hashResult);
    }

}