package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import com.jeuxolympiques.billetterie.exceptions.EmptyVerificationPhotoException;
import com.jeuxolympiques.billetterie.exceptions.VerificationPhotoNotFoundException;
import com.jeuxolympiques.billetterie.repositories.VerificationPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationPhotoService {

    private final VerificationPhotoRepository verificationPhotoRepository;
    private final ImageService imageService;

    // On prépare une constante pour gérer l'upload des photos de vérification
    private static final String UPLOAD_DIRECTORY = "uploads/verification/";

    /**
     * Méthode pour renvoyer l'ensemble des photos de vérification sous forme de liste
     * @return List<> des photos de verification
     */
    public List<VerificationPhoto> getAllVerificationPhotos(){
        List<VerificationPhoto> listOfAllPhotos = verificationPhotoRepository.findAll();
        return listOfAllPhotos.stream()
                .filter(verificationPhoto -> verificationPhoto.getUrl() != null)
                .toList();
    }

    /**
     * Méthode pour récupérer une photo de vérification depuis son id
     * @param id Identifiant de la photo de verification cherchée
     * @return La photo de vérification correspondante à l'id
     */
    public VerificationPhoto getVerificationPhotoById(String id){
        Optional<VerificationPhoto> verificationPhoto = verificationPhotoRepository.findById(id);
        if(verificationPhoto.isPresent()){
           return verificationPhoto.get();
        }
        throw new VerificationPhotoNotFoundException("La photo de vérification cherchée n'a pas été trouvée.");
    }

    /**
     * On met en place la méthode pour uploader les photos du repertoire
     * @param customer Client lié à la photo de vérification
     * @param imageFile Image à upload en tant photo de vérification
     * @return La photo de vérification enregistrée en base de données
     * @throws IOException
     */
    public VerificationPhoto uploadVerificationPhoto(Customer customer, MultipartFile imageFile) throws IOException {
        if(!imageFile.isEmpty()) {
            VerificationPhoto verificationPhoto = customer.getVerificationPhoto();
            verificationPhoto.setUrl(imageService.uploadImage(imageFile, UPLOAD_DIRECTORY));

            return verificationPhotoRepository.save(verificationPhoto);
        }
        throw new EmptyVerificationPhotoException("Il n'y a aucune photo de vérification à upload.");
    }

    /**
     * On met en place la méthode pour supprimer les photos du repertoire
     * @param customer Client de la photo de vérification à supprimer
     * @throws IOException
     */
    public VerificationPhoto deleteVerificationPhoto (Customer customer) throws IOException {
        VerificationPhoto verificationPhoto = customer.getVerificationPhoto();
        imageService.deleteImage(verificationPhoto.getUrl());
        verificationPhoto.setUrl(null);

        return verificationPhotoRepository.save(verificationPhoto);
    }

    /**
     * Méthode pour récupérer le client depuis l'id d'une photo
     * @param photoId Identifiant de la photo de vérification dont on veut récupérer le client
     * @return
     */
    public Customer getCustomerFromVerificationPhotoId(String photoId){
        VerificationPhoto verificationPhoto = this.getVerificationPhotoById(photoId);

        return verificationPhoto.getCustomer();
    }
}
