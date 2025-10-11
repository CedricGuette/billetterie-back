package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.exceptions.FileExtensionException;
import com.jeuxolympiques.billetterie.exceptions.FileSizeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static final String UPLOAD_PATH = "src/main/resources/static/";

    /**
     * Méthode pour upload une image sur le serveur
     * @param imageFile Image à upload
     * @param directory chemin où enregistrer l'image
     * @return l'url de l'image
     * @throws IOException
     */
    public String uploadImage(MultipartFile imageFile, String directory) throws IOException {
            // On vérifie que le repertoire existe bien sinon on le crée
            File uploadDirectory = new File(UPLOAD_PATH + directory);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // On vérifie l'extension du fichier
            if(!(imageFile.getContentType().equals("image/jpg") || imageFile.getContentType().equals("image/jpeg") || imageFile.getContentType().equals("image/png"))){
                throw new FileExtensionException("L'extension du fichier n'est pas acceptée.");
            }

            // On vérifie la taille de l'image
            if(imageFile.getSize() > 2097152){
                throw new FileSizeException("Le fichier image est trop volumineux.");
            }

            // On génère un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_PATH + directory + fileName);
            Files.write(filePath, imageFile.getBytes());

            return "/" + directory + fileName;
    }

    /**
     * Méthode pour remplacer une image en supprimant l'ancienne
     * @param imageFile Image à mettre à jour
     * @param directory localisation de l'image
     * @param previousImage Image à supprimer
     * @return l'url de la nouvelle image
     * @throws IOException
     */
    public String updateImage(MultipartFile imageFile, String directory, String previousImage) throws IOException {
        deleteImage(previousImage);
        return uploadImage(imageFile, directory);
    }

    /**
     * Méthode pour supprimer une image du serveur
     * @param imageUrl Image à supprimer
     * @throws IOException
     */
    public void deleteImage(String imageUrl) throws IOException {
        if(imageUrl != null){
            Path photoToDeletePath = Paths.get(UPLOAD_PATH + imageUrl.substring(1));
            if(Files.exists(photoToDeletePath)) {
                Files.delete(photoToDeletePath);
            }
        }
    }
}
