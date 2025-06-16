package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public final class Security extends User {

    @OneToMany(mappedBy = "security", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();
}
