package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public final class Moderator extends User{

    @JsonView(Views.Moderator.class)
    @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL)
    private List<VerificationPhoto> verificationPhoto = new ArrayList<>();
}
