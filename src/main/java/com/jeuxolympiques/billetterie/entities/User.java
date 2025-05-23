package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @UuidGenerator
    private String id;
    private String username;
    private String password;
    private String role;
    private String createdDate;

}
