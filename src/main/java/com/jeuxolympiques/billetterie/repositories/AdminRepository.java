package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
}
