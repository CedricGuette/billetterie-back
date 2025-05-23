package com.jeuxolympiques.billetterie.services;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {
    /*
    * Methode pour transformer une chaine de caractère en hash en haxadécimal
     */
    public static byte[] getHashHexadecimal(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        // la methode retourne un hash en hexadecimal
        return messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Methode pour convertir le hash d'hexadécimal en chaine de caractère
     */
    public static String fromHextoString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);

        StringBuilder stringFromHex = new StringBuilder(number.toString(16));
        while (stringFromHex.length() < 64) {
            stringFromHex.insert(0,0);
        }

        return stringFromHex.toString();
    }

    // Methode simplifiée pour hasher
    public static String toHash(String input) throws NoSuchAlgorithmException {
        return fromHextoString(getHashHexadecimal(input));
    }
}
