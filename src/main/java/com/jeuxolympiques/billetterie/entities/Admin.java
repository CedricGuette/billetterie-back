package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Admin extends User{
    public Admin(String id, String username, String password, String role, LocalDateTime createdDate, Boolean firstLogin) {
        super(id, username, password, role, createdDate);
        this.firstLogin = firstLogin;
    }

    @Column(columnDefinition = "boolean default true")
    private boolean firstLogin;
}
