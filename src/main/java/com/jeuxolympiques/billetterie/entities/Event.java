package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @UuidGenerator
    @JsonView({Views.Customer.class, Views.Admin.class})
    private String id;

    @NotNull
    @JsonView({Views.Customer.class, Views.Admin.class})
    private String name;

    @Lob
    @NotNull
    @JsonView({Views.Customer.class, Views.Admin.class})
    private String description;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private LocalDateTime date;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private String image;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private int amount;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private int ticketLeft;

    @NotNull
    @JsonView({Views.Customer.class, Views.Admin.class})
    private double soloPrice;

    @NotNull
    @JsonView({Views.Customer.class, Views.Admin.class})
    private double duoPrice;

    @NotNull
    @JsonView({Views.Customer.class, Views.Admin.class})
    private double familyPrice;

    // On crée un lien avec l'ensemble des tickets qui lui sont attribués
    @JsonIgnore
    @JsonView({Views.Customer.class, Views.Admin.class})
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

}
