package com.jeuxolympiques.billetterie.controllers;

import com.google.zxing.WriterException;
import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import com.jeuxolympiques.billetterie.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private HttpHeadersCORS headersCORS = new HttpHeadersCORS();

    @PatchMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> ticketIsPayed(@PathVariable String id) throws IOException, NoSuchAlgorithmException, WriterException {
        ticketService.ticketPayed(id);
        return ResponseEntity.status(HttpStatus.OK).header(String.valueOf(headersCORS.headers())).body("Votre ticket a bien été généré, vous le retrouverez dans votre espace personnel.");
    }
}
