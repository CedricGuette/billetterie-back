package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Security;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRepository extends JpaRepository<Security, String> {
}
