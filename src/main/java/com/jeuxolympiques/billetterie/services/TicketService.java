package com.jeuxolympiques.billetterie.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.exceptions.TicketNotFoundException;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // On crée la constante pour le chemin du dossier où seront contenues les qrcodes et les pdf
    private static final String GENERATOR_DIRECTORY = "tickets/";
    private static final String UPLOAD_PATH = "src/main/resources/static/";
    private static final String QR_DIRECTORY = "qrcodes/";
    private static final String PDF_DIRECTORY = "pdf/";

    /*
    * Méthode pour récupérer un ticket depuis son id
    */
    public Ticket getTicketById(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent()){
            return ticket.get();
        }
        throw new TicketNotFoundException("Le ticket que vous cherchez n'a pas été trouvé.");
    }

    /*
    * Méthode pour mettre à jour les informations du ticket
    */
    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /*
     *   Methode pour formater la création d'un ticket en base de données
     */
    public Ticket createTicket(Ticket ticket, Customer customer) {
        ticket.setTicketIsUsed(false);
        ticket.setQrCodeUrl(null);
        ticket.setTicketIsPayed(false);
        ticket.setTicketUrl(null);
        ticket.setSellingKey(null);
        ticket.setTicketValidationDate(null);
        ticket.setTicketValidationDate(null);
        ticket.setCustomer(customer);

        return ticketRepository.save(ticket);
    }

    /*
    *   Methode pour valider le paiement dans la base de données
    */
    public Ticket ticketPayed(String id) throws IOException, NoSuchAlgorithmException, WriterException {
        Ticket ticket = this.getTicketById(id);

        // On génère ici la deuxième clef pour créer la place
        UUID sellingKey = UUID.randomUUID();

        ticket.setSellingKey(sellingKey.toString());
        ticket.setTicketIsPayed(true);

        // On appelle les méthodes pour générer le ticket
        this.pdfGeneration(id);
        return ticketRepository.save(ticket);
    }
    /*
    * Méthode pour créer un dossier
    */
    private void createFolder(String path){

        // On vérifie si le dossier existe, sinon on le crée
        File uploadDirectory = new File(path);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
    }

    /*
    * Methode pour génerer un QR code avec les clefs avant de créer le pdf
    */
    private void qrGeneration(String id) throws IOException, WriterException, NoSuchAlgorithmException {
        Ticket ticket = this.getTicketById(id);
        Customer customer = ticket.getCustomer();

        // On récupère les clefs générées
        String customerKey = customer.getCustomerKey();
        String sellingKey = ticket.getSellingKey();

        // On les hash une fois chacun puis en faisant un hash de l'ensemble
        String keysHashed = HashService.toHash(customerKey) + HashService.toHash(sellingKey);
        keysHashed = HashService.toHash(keysHashed);
        //On crée la combninaison finale
        String encodedToQr = ticket.getId() + keysHashed;

        String fileName = System.currentTimeMillis() + "_" + "qr_code.png";
        String fileLocation = UPLOAD_PATH + GENERATOR_DIRECTORY + QR_DIRECTORY;
        ticket.setQrCodeUrl(fileLocation + fileName);

        // On vérifie si le dossier existe, sinon on le crée
        this.createFolder(fileLocation);

        String outputFilepath = fileLocation + fileName;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(encodedToQr,
                BarcodeFormat.QR_CODE, 250, 250);

        MatrixToImageWriter.writeToPath(bitMatrix, "png", Path.of(outputFilepath));

        ticketRepository.save(ticket);

    }

    /*
    * Méthode pour générer le pdf du ticket
    */
    private void pdfGeneration(String id) throws IOException, NoSuchAlgorithmException, WriterException {
        qrGeneration(id);
        Ticket ticket = this.getTicketById(id);

        Customer customer = ticket.getCustomer();
        String fileName = System.currentTimeMillis() + "_ticket.pdf";
        String documentLocation = UPLOAD_PATH + GENERATOR_DIRECTORY + PDF_DIRECTORY;
        String documentOnServer = GENERATOR_DIRECTORY + PDF_DIRECTORY;
        String imageUrl = ticket.getQrCodeUrl();

        if(imageUrl == null){
            throw new FileNotFoundException("Le QR code est introuvable.");
        }

        // On vérifie que le dossier qui va accueillir le pdf existe sinon on le crée
        this.createFolder(documentLocation);

        // Mise en page du PDF
        try (Document document = new Document(new PdfDocument(new PdfWriter(documentLocation + fileName)))) {
            Text title = new Text(String.format("Ticket valable pour %d personnes.", ticket.getHowManyTickets()));
            title.setBold();
            title.setFontSize(20);
            Image image = new Image(ImageDataFactory.create(imageUrl));
            document.add(new Paragraph(title));
            document.add(image);
            Text name = new Text(String.format("Nom: %S Prénom: %s",customer.getLastName(), customer.getFirstName()));
            document.add(new Paragraph(name));
            // On met à jour les informations dans la base de données
            ticket.setTicketUrl(documentOnServer + fileName);
            ticket.setTicketCreatedDate(LocalDateTime.now());

            // On supprime le QRcode une fois terminé
            ticket.setQrCodeUrl("");
            Path qrToDeletePath = Paths.get(imageUrl);
            if(Files.exists(qrToDeletePath)) {
                Files.delete(qrToDeletePath);
            }
            ticketRepository.save(ticket);
        }
    }
}
