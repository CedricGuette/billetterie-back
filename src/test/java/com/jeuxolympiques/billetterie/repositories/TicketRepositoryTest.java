package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void shouldGetAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();

        assertEquals(6, tickets.size());
    }

    @Test
    void shouldGetTicketById() {
        Ticket ticket = ticketRepository.findById("7d5303f1-99c8-4eae-8376-4d741ba268eb").get();

        assertEquals(1, ticket.getEventCode());
        assertEquals(4, ticket.getHowManyTickets());
        assertEquals("", ticket.getQrCodeUrl());
        assertEquals("tickets/pdf/1750445253345_ticket.pdf", ticket.getTicketUrl());
        assertEquals(true, ticket.getTicketIsPayed());
        assertEquals(false, ticket.getTicketIsUsed());
        assertEquals(LocalDateTime.parse("2025-06-20T20:47:33.472499"), ticket.getTicketCreatedDate());
    }

    @Test
    void shouldCreateTicket() {
        Ticket ticket = new Ticket();
        Customer customer = new Customer();

        ticket.setTicketIsPayed(false);
        ticket.setTicketIsUsed(false);
        ticket.setEventCode(1);
        ticket.setHowManyTickets(2);
        ticket.setCustomer(customer);
        ticket.setTicketCreatedDate(LocalDateTime.parse("2025-06-20T20:47:33.472499"));

        Ticket savedTicket = ticketRepository.save(ticket);

        assertNotNull(savedTicket.getId());
        assertEquals(false, savedTicket.getTicketIsPayed());
        assertEquals(false, savedTicket.getTicketIsUsed());
        assertEquals(1, savedTicket.getEventCode());
        assertEquals(2, savedTicket.getHowManyTickets());
        assertEquals(customer, savedTicket.getCustomer());
        assertEquals(LocalDateTime.parse("2025-06-20T20:47:33.472499"), savedTicket.getTicketCreatedDate());

    }

    @Test
    void shouldUpdateTicket() {
        Ticket ticket = ticketRepository.findById("b8aa6406-b61c-4d16-9671-5db6943c9172").get();
        ticket.setTicketUrl("");
        ticket.setHowManyTickets(4);

        Ticket savedTicket = ticketRepository.save(ticket);

        assertEquals("", savedTicket.getTicketUrl());
        assertEquals(4, savedTicket.getHowManyTickets());
    }

    @Test
    void shouldDeleteTicketById() {
        ticketRepository.deleteById("80947444-62df-4ac4-aae9-97dcdc27a812");
        Optional<Ticket> deletedTicket = ticketRepository.findById("80947444-62df-4ac4-aae9-97dcdc27a812");

        assertFalse(deletedTicket.isPresent());
    }
}