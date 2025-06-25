package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public final class Security extends User {

    @OneToMany(mappedBy = "security", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    public Security(String id, String username, String password, String role, LocalDateTime createdDate) {
        super(id, username, password, role, createdDate);
    }
}
