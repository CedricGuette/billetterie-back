package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldReturnAllEvents() {
        List<Event> events = eventRepository.findAll();

        assertEquals(2, events.size());
    }

    @Test
    void shouldGetEventById() {
        Event event = eventRepository.findById("67207e92-dc13-4a29-8f41-d96c3e191b98").get();

        assertEquals("Finale football masculin France - Espagne", event.getName());
        assertEquals(44260, event.getAmount());
        assertEquals(160, event.getFamilyPrice());
    }

    @Test
    void shouldSaveEvent() {
        Event event = new Event();
        event.setName("Series 400 mètres 4 nages masculin");
        event.setDescription("Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        event.setDate(LocalDateTime.parse("2024-07-28 11:00", formatter));
        event.setAmount(17000);
        event.setTicketLeft(17000);
        event.setImage("/uploads/event/natation2.jpeg");
        event.setSoloPrice(35);
        event.setDuoPrice(60);
        event.setFamilyPrice(110);

        Event savedEvent = eventRepository.save(event);

        assertNotNull(savedEvent.getId());
        assertEquals("Series 400 mètres 4 nages masculin", savedEvent.getName());
        assertEquals(17000, savedEvent.getTicketLeft());
        assertEquals(110, savedEvent.getFamilyPrice());

    }

    @Test
    void shouldUpdateEvent(){
        Event event = eventRepository.findById("67207e92-dc13-4a29-8f41-d96c3e191b98").get();

        event.setDuoPrice(150);
        Event updatedEvent = eventRepository.save(event);

        assertEquals(150, updatedEvent.getDuoPrice());

    }

    @Test
    void shouldDeleteEvent(){
        eventRepository.deleteById("67207e92-dc13-4a29-8f41-d96c3e191b98");

        Optional<Event> deletedEvent = eventRepository.findById("67207e92-dc13-4a29-8f41-d96c3e191b98");

        assertFalse(deletedEvent.isPresent());
    }
}