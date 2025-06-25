package com.jeuxolympiques.billetterie.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @UuidGenerator
    @JsonView(Views.Admin.class)
    private String id;

    @NotNull
    @Size(max = 320)
    @Email(regexp = "[a-z0-9._%+-]+@\\.[a-z]{2,3}",
                      flags = Pattern.Flag.CASE_INSENSITIVE)
    @JsonView({Views.Customer.class, Views.Moderator.class, Views.Admin.class})
    private String username;

    private String password;

    @JsonView(Views.UserRole.class)
    private String role;

    @JsonView({Views.Customer.class, Views.Moderator.class, Views.Admin.class})
    private LocalDateTime createdDate;

    public enum Role {
        ROLE_USER, ROLE_MODERATOR, ROLE_SECURITY, ROLE_ADMIN
    }
}
