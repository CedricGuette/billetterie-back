package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String id;
    private Integer eventCode;
    private Integer howManyTickets;
    private Boolean ticketIsPayed;
    private Boolean ticketIsUsed;
    private String sellingKey;
    private String qrCodeUrl;
    private String ticketUrl;
    private String ticketCreatedDate;
    private String ticketValidationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "security_id")
    private Security security;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
}
