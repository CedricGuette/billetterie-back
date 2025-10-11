package com.jeuxolympiques.billetterie.controllers;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.entities.Event;
import com.jeuxolympiques.billetterie.services.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "${URL_FRONT}")
public class EventController {

    private final EventService eventService;

    private final HttpHeadersCORS httpHeaders = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    /**
     * Requête pour récupérer l'ensemble des évènements
     * @return List<> de l'ensemble des évènements
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(eventService.getAllEvents());
    }

    /**
     * Requête pour récupérer un évènement selon son id
     * @param id Identifiant de l'évènement recherché
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(eventService.getEventById(id));
    }

    /**
     * Requête pour créer un évènement
     * @param event Informations de l'évènement à créer
     * @param imageFile Image de l'évènement à upload
     * @return
     * @throws IOException
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> createEvent(@RequestPart Event event, @RequestPart("image")  MultipartFile imageFile) throws IOException {
        eventService.createEvent(imageFile, event);

        Map<String, String> response = new HashMap<>();
        response.put("created", STR."L'évènement \{event.getName()} a bien été créé.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }

    /**
     * Requête pour mettre à jour un évènement
     * @param id Identifiant de l'évènement à mettre à jour
     * @param event Informations de l'évènement à mettre à jour
     * @param imageFile Image de l'évènement à mettre à jour
     * @return
     * @throws IOException
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateEvent(@PathVariable String id, @RequestPart Event event, @RequestPart(name = "image", required = false) MultipartFile imageFile) throws IOException {
        if(imageFile != null){
            eventService.updateImage(imageFile, id);
        }

        eventService.updateEvent(id, event);

        Map<String, String> response = new HashMap<>();
        response.put("updated", STR."L'évènement \{event.getName()} a bien été mis à jour.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }

    /**
     * Requête pour supprimer un évènement
     * @param id Identifiant de l'évènement à supprimer
     * @return
     * @throws IOException
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable String id) throws IOException {
        eventService.deleteEvent(id);

        Map<String, String> response = new HashMap<>();
        response.put("deleted", STR."L'évènement avec l'id \{id} a bien été supprimé.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(String.valueOf(httpHeaders.headers()))
                .body(response);
    }
}
