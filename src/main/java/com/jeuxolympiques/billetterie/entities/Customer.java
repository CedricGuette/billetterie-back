package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public final class Customer extends User{
    // On définit les variables qui composent le profil de base d'un client
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean profileIsValidate;
    private String customerKey;

    // On crée un lien avec la photo de vérification
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_photo_id")
    private VerificationPhoto verificationPhoto = new VerificationPhoto();
    // On crée un lien avec l'ensemble des tickets qui lui sont attribués
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();
}
