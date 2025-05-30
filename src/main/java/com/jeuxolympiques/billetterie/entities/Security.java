package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public final class Security extends User {

    @OneToMany(mappedBy = "security", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();
}
