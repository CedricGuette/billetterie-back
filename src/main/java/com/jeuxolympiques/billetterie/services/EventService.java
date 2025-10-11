package com.jeuxolympiques.billetterie.services;

import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.exceptions.EventNotFoundException;
import com.jeuxolympiques.billetterie.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    // On importe le repository concerné
    private final EventRepository eventRepository;
    private final ImageService imageService;

    // On prépare une constante pour gérer l'upload des photos de vérification
    private static final String UPLOAD_DIRECTORY = "uploads/event/";

    /**
     * Méthode pour récupérer la liste des évènements
     * @return List des évènements
     */
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    /**
     * Méthode pour récupérer un évènement par son id
     * @param id Identifiant de l'évènement recherché
     * @return Évènement correspondant à l'id
     */
    public Event getEventById(String id){
        Optional<Event> event = eventRepository.findById(id);

        if(event.isPresent()){
            return event.get();
        }

        throw new EventNotFoundException("L'évènement que vous cherchez n'existe pas.");
    }

    /**
     * Méthode pour créer un évènement
     * @param imageFile Image de l'évènement
     * @param event informations de l'évènement à créer
     * @return L'évènement enregistré en base de données
     * @throws IOException
     */
    public Event createEvent(MultipartFile imageFile, Event event) throws IOException {
        event.setImage(imageService.uploadImage(imageFile, UPLOAD_DIRECTORY));
        event.setTicketLeft(event.getAmount());
        Event createdEvent = eventRepository.save(event);

        return createdEvent;
    }

    /**
     * Méthode pour créer l'évènement initial au lancement de l'application
     * @param event Informations de l'évènement à créer
     * @return l'évènement enregistré en base de données
     */
    public Event createInitialEvent(Event event){
        Event createdEvent = eventRepository.save(event);

        return createdEvent;
    }

    /**
     * Méthode pour mettre à jour un évènement
     * @param id Identifiant de l'évènement à modifier
     * @param event Informations à mettre à jour
     * @return L'évènement enregistré en base de données
     */
    public Event updateEvent(String id, Event event){
        Optional<Event> eventInDatabase = eventRepository.findById(id);

        if(eventInDatabase.isPresent()){
            Event eventToUpdate = eventInDatabase.get();

            eventToUpdate.setName(event.getName());
            eventToUpdate.setDescription(event.getDescription());
            eventToUpdate.setDate(event.getDate());
            eventToUpdate.setAmount(event.getAmount());
            eventToUpdate.setSoloPrice(event.getSoloPrice());
            eventToUpdate.setDuoPrice(event.getDuoPrice());
            eventToUpdate.setFamilyPrice(event.getFamilyPrice());

            Event eventUpdated = eventRepository.save(eventToUpdate);

            return eventUpdated;
        }

        throw new EventNotFoundException("L'évènement que vous cherchez n'existe pas.");
    }

    /**
     * Méthode pour mettre à jour l'image pour un évènement
     * @param imageFile Image à mettre à jour
     * @param id Identifiant de l'évènement à mettre à jour
     * @return l'évènement enregistré en base de données
     * @throws IOException
     */
    public Event updateImage(MultipartFile imageFile, String id) throws IOException {
        Event event = getEventById(id);
        event.setImage(imageService.updateImage(imageFile, UPLOAD_DIRECTORY, event.getImage()));
        Event updatedEvent = eventRepository.save(event);
        return updatedEvent;
    }

    /**
     * Méthode pour effacer un événement
     * @param id Identifiant de l'évènement à supprimer
     * @throws IOException
     */
    public void deleteEvent(String id) throws IOException {
        Optional<Event> eventToDelete = eventRepository.findById(id);

        if(eventToDelete.isPresent()){
            Event event = eventToDelete.get();
            imageService.deleteImage(event.getImage());
            eventRepository.delete(event);
        }else{
            throw new EventNotFoundException("L'évènement que vous cherchez n'existe pas.");
        }
    }

    /**
     * Méthode qui crée le premier évènement au lancement de l'app s'il n'existe pas déjà
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initiateEvent() {
        if(eventRepository.count() == 0){
            Event firstEvent = new Event();
            firstEvent.setName("Finale football masculin France - Espagne");
            firstEvent.setDescription("Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l'Espagne se retrouvent en finale pour se disputer l'or pendant un match qui restera sans aucun doute dans les mémoires.");
            firstEvent.setAmount(44260);
            firstEvent.setTicketLeft(firstEvent.getAmount());
            firstEvent.setDate(LocalDateTime.of(2024, 8, 9, 18, 0, 0));
            firstEvent.setSoloPrice(50);
            firstEvent.setDuoPrice(90);
            firstEvent.setFamilyPrice(160);
            firstEvent.setImage("/" + UPLOAD_DIRECTORY +"initial.jpeg");

            createInitialEvent(firstEvent);
        }
    }
}
