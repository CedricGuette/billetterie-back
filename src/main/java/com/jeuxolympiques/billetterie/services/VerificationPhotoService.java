package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class VerificationPhotoService {

    // On prépare une constante pour gérer l'upload des photos de vérification
    private static String UPLOAD_DIRECTORY = "uploads/";
    private static String UPLOAD_PATH = "src/main/resources/static/";

    /*
     * On met en place la méthode pour uploader les photos du repertoire
     */
    public void uploadVerificationPhoto(Customer customer, MultipartFile imageFile) throws IOException {
        if(!imageFile.isEmpty()) {
            // On vérifie que le repertoire existe bien sinon on le crée
            File uploadDirectory = new File(UPLOAD_PATH + UPLOAD_DIRECTORY);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // On génère un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_PATH + UPLOAD_DIRECTORY + fileName);
            Files.write(filePath, imageFile.getBytes());

            VerificationPhoto verificationPhoto = customer.getVerificationPhoto();
            verificationPhoto.setUrl("/" + UPLOAD_DIRECTORY + fileName);
        }
    }

    /*
    * On met en place la méthode pour supprimer les photos du repertoire
    */
    public void deleteVerificationPhoto (Customer customer) throws IOException {
        VerificationPhoto verificationPhoto = customer.getVerificationPhoto();
        Path photoToDeletePath = Paths.get(UPLOAD_PATH + verificationPhoto.getUrl().substring(1));
        if(Files.exists(photoToDeletePath)) {
            Files.delete(photoToDeletePath);
        }
    }
}
