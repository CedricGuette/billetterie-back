package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.Admin.class)
    private String id;

    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String username;

    private String password;

    @JsonView(Views.UserRole.class)
    private String role;

    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String createdDate;

}
