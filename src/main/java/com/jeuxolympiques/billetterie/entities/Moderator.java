package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public final class Moderator extends User{

    @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL)
    private List<VerificationPhoto> verificationPhoto = new ArrayList<>();
}
