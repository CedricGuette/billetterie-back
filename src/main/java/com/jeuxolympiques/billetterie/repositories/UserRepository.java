package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
