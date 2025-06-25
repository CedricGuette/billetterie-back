package com.jeuxolympiques.billetterie.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Admin extends User{
    public Admin(String id, String username, String password, String role, LocalDateTime createdDate) {
        super(id, username, password, role, createdDate);
    }
}
