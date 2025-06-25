package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public final class Customer extends User{

    @NotNull
    @Pattern(regexp = "[a-zA-ZÀ-ÖØ-öø-ÿ]{2,50}")
    @JsonView({Views.Customer.class, Views.Moderator.class, Views.Admin.class})
    private String firstName;

    @NotNull
    @Pattern(regexp = "[a-zA-ZÀ-ÖØ-öø-ÿ]{2,50}")
    @JsonView({Views.Customer.class, Views.Moderator.class, Views.Admin.class})
    private String lastName;

    @NotNull
    @Pattern(regexp = "\\d{10}")
    @JsonView({Views.Customer.class, Views.Admin.class})
    private String phoneNumber;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private Boolean profileIsValidate;

    @JsonView(Views.Admin.class)
    private String customerKey;

    // On crée un lien avec la photo de vérification
    @JsonView(Views.Admin.class)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_photo_id")
    private VerificationPhoto verificationPhoto = new VerificationPhoto();

    // On crée un lien avec l'ensemble des tickets qui lui sont attribués
    @JsonView({Views.Customer.class, Views.Admin.class})
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    public Customer(String id, String username, String password, String role, LocalDateTime createdDate, String firstName, String lastName, String phoneNumber,
                    Boolean profileIsValidate, String customerKey, VerificationPhoto verificationPhoto, List<Ticket> tickets) {
        super(id, username, password, role, createdDate);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileIsValidate = profileIsValidate;
        this.customerKey = customerKey;
        this.verificationPhoto = verificationPhoto;
        this.tickets = tickets;
    }
}
