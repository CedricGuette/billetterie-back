package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_photo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationPhoto {

    @JsonView(Views.Moderator.class)
    @Id
    @UuidGenerator
    private String id;

    @JsonView(Views.Moderator.class)
    private String url;

    @JsonView(Views.Admin.class)
    private LocalDateTime verificationDate;

    @JsonView({Views.Moderator.class})
    @OneToOne(mappedBy = "verificationPhoto", cascade = CascadeType.ALL)
    private Customer customer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name="moderator_id")
    private Moderator moderator;

}
