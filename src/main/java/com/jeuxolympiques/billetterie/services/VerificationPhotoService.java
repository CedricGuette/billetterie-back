package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.exceptions.EmptyVerificationPhotoException;
import com.jeuxolympiques.billetterie.exceptions.VerificationPhotoNotFoundException;
import com.jeuxolympiques.billetterie.repositories.VerificationPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationPhotoService {

    private final VerificationPhotoRepository verificationPhotoRepository;

    // On prépare une constante pour gérer l'upload des photos de vérification
    private static final String UPLOAD_DIRECTORY = "uploads/";
    private static final String UPLOAD_PATH = "src/main/resources/static/";

    /*
    * Méthode pour renvoyer l'ensemble des photos de vérification sous forme de liste
    */
    public List<VerificationPhoto> getAllVerificationPhotos(){
        List<VerificationPhoto> listOfAllPhotos = verificationPhotoRepository.findAll();
        return listOfAllPhotos.stream()
                .filter(verificationPhoto -> verificationPhoto.getUrl() != null)
                .toList();
    }

    /*
    * Méthode pour récupérer une photo de vérification depuis son id
    */
    public VerificationPhoto getVerificationPhotoById(String id){
        Optional<VerificationPhoto> verificationPhoto = verificationPhotoRepository.findById(id);
        if(verificationPhoto.isPresent()){
           return verificationPhoto.get();
        }
        throw new VerificationPhotoNotFoundException("La photo de vérification cherchée n'a pas été trouvée.");
    }

    /*
     * On met en place la méthode pour uploader les photos du repertoire
     */
    public VerificationPhoto uploadVerificationPhoto(Customer customer, MultipartFile imageFile) throws IOException {
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

            return verificationPhotoRepository.save(verificationPhoto);
        }
        throw new EmptyVerificationPhotoException("Il n'y a aucune photo de vérification à upload.");
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

    /*
    * Méthode pour récupérer le client depuis l'id d'une photo
    */
    public Customer getCustomerFromVerificationPhotoId(String photoId){
        VerificationPhoto verificationPhoto = this.getVerificationPhotoById(photoId);

        return verificationPhoto.getCustomer();
    }
}
