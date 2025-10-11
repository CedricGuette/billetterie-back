package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public final class Moderator extends User{

    @JsonView(Views.Moderator.class)
    @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL)
    private List<VerificationPhoto> verificationPhoto = new ArrayList<>();

    public Moderator(String id, String username, String password, String role, LocalDateTime createdDate) {
        super(id, username, password, role, createdDate);
    }
}
