package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
}
