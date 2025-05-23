package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_photo")
@Data
public class VerificationPhoto {
    @Id
    @UuidGenerator
    private String id;
    private String url;
    private String verificationDate;

    @JsonIgnore
    @OneToOne(mappedBy = "verificationPhoto")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name="moderator_id")
    private Moderator moderator;
}
