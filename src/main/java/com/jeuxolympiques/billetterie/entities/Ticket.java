package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name="tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @UuidGenerator
    @JsonView({Views.Customer.class, Views.Admin.class})
    private String id;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private Integer eventCode;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private Integer howManyTickets;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private Boolean ticketIsPayed;

    @JsonView({Views.Admin.class})
    private Boolean ticketIsUsed;

    @JsonView({Views.Admin.class})
    private String sellingKey;

    private String qrCodeUrl;

    @JsonView({Views.Customer.class})
    private String sessionId;

    @JsonView({Views.Customer.class})
    private String sessionClientSecret;

    @JsonView({Views.Customer.class})
    private LocalDateTime sessionCreatedDate;

    @JsonView(Views.Customer.class)
    private String ticketUrl;

    @JsonView({Views.Customer.class, Views.Admin.class})
    private LocalDateTime ticketCreatedDate;

    @JsonView(Views.Admin.class)
    private LocalDateTime ticketValidationDate;

    @JsonView(Views.Admin.class)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "security_id")
    private Security security;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="customer_id")
    private Customer customer;
}
