package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, String> {
}
