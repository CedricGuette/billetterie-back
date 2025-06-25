package com.jeuxolympiques.billetterie.services;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;

    @InjectMocks
    TicketService ticketService;


    @Test
    void shouldReturnTicketById() {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,1,true,false,"2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W","cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,"tickets/pdf/1750445549179_ticket.pdf", LocalDateTime.parse("2025-06-20T20:52:29.307152"), LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null);

        when(ticketRepository.findById("317419e5-beb9-4e0e-b471-0bd551865034")).thenReturn(Optional.of(ticket1));

        Ticket ticket = ticketService.getTicketById("317419e5-beb9-4e0e-b471-0bd551865034");

        assertThat(ticket).isEqualTo(ticket1);
    }

    @Test
    void shouldUpdateTicket() {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,1,true,false,"2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba",null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W","cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",null,"tickets/pdf/1750445549179_ticket.pdf", LocalDateTime.parse("2025-06-20T20:52:29.307152"), LocalDateTime.parse("2025-06-20T20:52:11.121479"),null,null);
        when(ticketRepository.save(ticket1)).thenReturn(ticket1);

        ticket1.setTicketIsPayed(false);
        Ticket ticketSaved = ticketService.updateTicket(ticket1);

        assertThat(ticketSaved.getTicketIsPayed()).isFalse();
    }

    @Test
    void  shouldCreateTicket() {
        Ticket ticket1 = new Ticket(null,1,1,null,null,null,null,null,null,null,null, null, null,null,null);
        Customer customer1 = new Customer("d64400c8-8054-4a77-9908-250c55036594","raph@ptdr.fr","$2a$10$M.iveOTFmzOXqKuymo9qJuoCM6tBiLORTclYgXQD2LZYho42zUaXS","ROLE_USER",LocalDateTime.parse("2025-06-20T20:51:54.840509"),"Raph","Aelle","0102050607",true,"57890899-cb3b-4fdd-96c7-fb17ab6bde20",null, null);
        when(ticketRepository.save(ticket1)).thenReturn(ticket1);

        Ticket savedTicket = ticketService.createTicket(ticket1, customer1);

        assertThat(savedTicket.getCustomer().getId()).isEqualTo("d64400c8-8054-4a77-9908-250c55036594");
        assertFalse(savedTicket.getTicketIsPayed());
        assertFalse(savedTicket.getTicketIsUsed());
    }

    @Test
    void shouldMakeATicketPayed() throws IOException, NoSuchAlgorithmException, WriterException {
        Ticket ticket1 = new Ticket("317419e5-beb9-4e0e-b471-0bd551865034",1,1,false,false,null,null,"cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W","cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl",LocalDateTime.parse("2025-06-20T20:52:29.307152"),null, null , null,null,null);
        Customer customer1 = new Customer("d64400c8-8054-4a77-9908-250c55036594","raph@ptdr.fr","$2a$10$M.iveOTFmzOXqKuymo9qJuoCM6tBiLORTclYgXQD2LZYho42zUaXS","ROLE_USER",LocalDateTime.parse("2025-06-20T20:51:54.840509"),"Raph","Aelle","0102050607",true,"57890899-cb3b-4fdd-96c7-fb17ab6bde20",null, null);
        ticket1.setCustomer(customer1);
        when(ticketRepository.save(ticket1)).thenReturn(ticket1);
        when(ticketRepository.findById("317419e5-beb9-4e0e-b471-0bd551865034")).thenReturn(Optional.of(ticket1));

        Ticket payedTicket = ticketService.ticketPayed("317419e5-beb9-4e0e-b471-0bd551865034");

        assertTrue(payedTicket.getTicketIsPayed());
        assertNotNull(payedTicket.getSellingKey());
        assertNotNull(payedTicket.getTicketUrl());
        assertThat(payedTicket.getQrCodeUrl()).isEmpty();
    }

}