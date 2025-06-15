package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name="tickets")
@Data
public class Ticket {

    @Id
    @UuidGenerator
    @JsonView({Views.User.class, Views.Admin.class})
    private String id;

    @JsonView({Views.User.class, Views.Admin.class})
    private Integer eventCode;

    @JsonView({Views.User.class, Views.Admin.class})
    private Integer howManyTickets;

    @JsonView({Views.User.class, Views.Admin.class})
    private Boolean ticketIsPayed;

    @JsonView({Views.Admin.class})
    private Boolean ticketIsUsed;

    @JsonView({Views.Admin.class})
    private String sellingKey;

    private String qrCodeUrl;

    @JsonView(Views.User.class)
    private String ticketUrl;

    @JsonView({Views.User.class, Views.Admin.class})
    private String ticketCreatedDate;

    @JsonView(Views.Admin.class)
    private String ticketValidationDate;

    @JsonView(Views.Admin.class)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "security_id")
    private Security security;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
}
