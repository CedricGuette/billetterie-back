package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.repositories.EventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    ImageService imageService;

    @InjectMocks
    EventService eventService;

    @Test
    void shouldReturnAllEvents(){
        Event event1 = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        Event event2 = new Event("134c0aa8-0c22-4c94-8edf-f81c333db574","Finale 400 mètres 4 nages masculin","Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.",
                LocalDateTime.parse("2024-07-28T20:30:00.000000"), "/uploads/event/natation.jpeg", 17000, 17000, 35, 90, 110, null);

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<Event> events = eventService.getAllEvents();

        assertThat(events).hasSize(2);
    }

    @Test
    void shouldReturnEventById() {
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        when(eventRepository.findById("43729766-67b3-47d2-80f7-6ab87e0d4e5b")).thenReturn(Optional.of(event));

        Event eventSearched = eventService.getEventById("43729766-67b3-47d2-80f7-6ab87e0d4e5b");

        assertThat(eventSearched).isEqualTo(event);
    }

    @Test
    void shouldCreateEvent() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );

        Event event = new Event(null,"Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), null, 44260, 44260, 50, 90, 160, null);

        when(eventRepository.save(event)).thenReturn(event);
        when(imageService.uploadImage(file,"uploads/event/")).thenReturn("uploads/event/image.jpg");

        Event createdEvent = eventService.createEvent(file, event);

        assertThat(createdEvent.getName()).isEqualTo("Finale football masculin France - Espagne");
        assertThat(createdEvent.getImage()).isEqualTo("uploads/event/image.jpg");
    }

    @Test
    void shouldCreateInitialEvent() {

        Event event = new Event(null,"Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), null, 44260, 44260, 50, 90, 160, null);
        when(eventRepository.save(event)).thenReturn(event);

        Event createdEvent = eventService.createInitialEvent(event);

        assertThat(createdEvent.getName()).isEqualTo("Finale football masculin France - Espagne");
        assertThat(createdEvent.getAmount()).isEqualTo(44260);
    }

    @Test
    void shouldUploadEvent() {

        Event event1 = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), null, 44000, 44000, 45, 90, 210, null);
        Event event2 = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), null, 44260, 44260, 50, 90, 160, null);

        when(eventRepository.findById("43729766-67b3-47d2-80f7-6ab87e0d4e5b")).thenReturn(Optional.of(event1));
        when(eventRepository.save(event1)).thenReturn(event1);

        Event eventUploaded = eventService.updateEvent("43729766-67b3-47d2-80f7-6ab87e0d4e5b", event2);

        assertThat(eventUploaded.getAmount()).isEqualTo(44260);
        assertThat(eventUploaded.getName()).isEqualTo("Finale football masculin France - Espagne");
        assertThat(eventUploaded.getSoloPrice()).isEqualTo(50);
        assertThat(eventUploaded.getFamilyPrice()).isEqualTo(160);
    }

    @Test
    void shouldUpdateEventImage() throws IOException {
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "{\"image\": \"C:\\Users\\renta\\Pictures\\image.jpg\"}".getBytes()
        );
        when(eventRepository.findById("43729766-67b3-47d2-80f7-6ab87e0d4e5b")).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(imageService.updateImage(file,"uploads/event/","/event/initial.jpeg")).thenReturn("/event/image.jpeg");

        Event eventWithUpdatedImage = eventService.updateImage(file,"43729766-67b3-47d2-80f7-6ab87e0d4e5b");

        assertThat(eventWithUpdatedImage.getImage()).isEqualTo("/event/image.jpeg");

    }

    @Test
    void shouldDeleteEvent() throws IOException {
        Event event = new Event("43729766-67b3-47d2-80f7-6ab87e0d4e5b","Finale football masculin France - Espagne","Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.",
                LocalDateTime.now(), "/event/initial.jpeg", 44260, 44260, 50, 90, 160, null);

        when(eventRepository.findById("43729766-67b3-47d2-80f7-6ab87e0d4e5b")).thenReturn(Optional.of(event));

        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                eventRepository.delete(event);
                return null;
            }
        }).when(Mockito.spy(eventService)).deleteEvent("43729766-67b3-47d2-80f7-6ab87e0d4e5b");

        eventService.deleteEvent("43729766-67b3-47d2-80f7-6ab87e0d4e5b");

        verify(eventRepository).delete(event);
    }
}