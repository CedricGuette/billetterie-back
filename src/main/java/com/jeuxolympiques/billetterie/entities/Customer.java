package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public final class Customer extends User{


    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String firstName;

    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String lastName;

    @JsonView({Views.User.class, Views.Admin.class})
    private String phoneNumber;

    @JsonView({Views.User.class, Views.Admin.class})
    private Boolean profileIsValidate;

    @JsonView(Views.Admin.class)
    private String customerKey;

    // On crée un lien avec la photo de vérification
    @JsonView(Views.Admin.class)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_photo_id")
    private VerificationPhoto verificationPhoto = new VerificationPhoto();

    // On crée un lien avec l'ensemble des tickets qui lui sont attribués
    @JsonView({Views.User.class, Views.Admin.class})
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();
}
