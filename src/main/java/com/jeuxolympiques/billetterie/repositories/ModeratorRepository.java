package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeratorRepository extends JpaRepository<Moderator, String> {
}
