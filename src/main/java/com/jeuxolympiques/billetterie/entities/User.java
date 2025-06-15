package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @UuidGenerator
    @JsonView(Views.Admin.class)
    private String id;

    @Email(regexp = "[a-z0-9._%+-]+@\\.[a-z]{2,3}",
                      flags = Pattern.Flag.CASE_INSENSITIVE)
    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String username;

    private String password;

    @JsonView(Views.UserRole.class)
    private String role;

    @JsonView({Views.User.class, Views.Moderator.class, Views.Admin.class})
    private String createdDate;

}
