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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.jeuxolympiques.billetterie.entities.Customer;
import com.jeuxolympiques.billetterie.entities.Ticket;
import com.jeuxolympiques.billetterie.repositories.CustomerRepository;
import com.jeuxolympiques.billetterie.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    // On crée la constante pour le chemin du dossier où seront contenues les qrcodes et les pdf
    private static String GENERATOR_DIRECTORY = "tickets/";
    private static String UPLOAD_PATH = "src/main/resources/static/";
    private static String QR_DIRECTORY = "qrcodes/";
    private static String PDF_DIRECTORY = "pdf/";

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
        return ticket;
    }

    /*
    *   Methode pour valider le paiement dans la base de données
    */
    public void ticketPayed(String id) throws IOException, NoSuchAlgorithmException, WriterException {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent()) {
            Ticket ticketPayed = ticket.get();
            // On génère ici la deuxième clef pour créer la place
            UUID sellingKey = UUID.randomUUID();
            ticketPayed.setSellingKey(sellingKey.toString());
            ticketPayed.setTicketIsPayed(true);

            // On appelle les méthodes pour générer le ticket
            pdfGeneration(id);
            ticketRepository.save(ticketPayed);
        }
    }

    /*
    * Methode pour génerer un QR code avec les clefs avant de créer le pdf
     */
    private Ticket qrGeneration(String id) throws IOException, WriterException, NoSuchAlgorithmException {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent()) {
            Ticket ticketToBeGenerated = ticket.get();
            Customer customer = ticketToBeGenerated.getCustomer();

            // On récupère les clefs générées
            String customerKey = customer.getCustomerKey();
            String sellingKey = ticketToBeGenerated.getSellingKey();

            // On les hash une fois chacun puis en faisant un hash de l'ensemble
            String keysHashed = HashService.toHash(customerKey) + HashService.toHash(sellingKey);
            keysHashed = HashService.toHash(keysHashed);
            //On crée la combninaison finale
            String encodedToQr = ticketToBeGenerated.getId() + keysHashed;

            String fileName = System.currentTimeMillis() + "_" + "qr_code.png";
            String fileLocation = UPLOAD_PATH + GENERATOR_DIRECTORY + QR_DIRECTORY;
            ticketToBeGenerated.setQrCodeUrl(fileLocation + fileName);

            // On vérifie si le dossier existe, sinon on le crée
            File uploadDirectory = new File(fileLocation);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String outputFilepath = fileLocation + fileName;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(encodedToQr,
                    BarcodeFormat.QR_CODE, 250, 250);

            MatrixToImageWriter.writeToPath(bitMatrix, "png", Path.of(outputFilepath));

            return ticketRepository.save(ticketToBeGenerated);
        }
        return null;
    }

    private Ticket pdfGeneration(String id) throws IOException, NoSuchAlgorithmException, WriterException {
        qrGeneration(id);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent()) {
            Ticket ticketToPdf = ticket.get();
            Customer customer = ticket.get().getCustomer();
            String fileName = System.currentTimeMillis() + "_ticket.pdf";
            String documentLocation = UPLOAD_PATH + GENERATOR_DIRECTORY + PDF_DIRECTORY;
            String documentOnServer = GENERATOR_DIRECTORY + PDF_DIRECTORY;
            String imageUrl = ticketToPdf.getQrCodeUrl();

            // On vérifie que le dossier qui va accueillir le pdf existe sinon on le crée
            File uploadDirectory = new File(documentLocation);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Mise en page du PDF
            try (Document document = new Document(new PdfDocument(new PdfWriter(documentLocation + fileName)))) {
                Text title = new Text(String.format("Ticket valable pour %d personnes.", ticket.get().getHowManyTickets()));
                title.setBold();
                title.setFontSize(20);
                Image image = new Image(ImageDataFactory.create(imageUrl));
                document.add(new Paragraph(title));
                document.add(image);
                Text name = new Text(String.format("Nom: %S Prénom: %s",customer.getLastName(), customer.getFirstName()));
                document.add(new Paragraph(name));
                // On met à jour les informations dans la base de données
                ticketToPdf.setTicketUrl(documentOnServer + fileName);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                ticketToPdf.setTicketCreatedDate(LocalDateTime.now().format(formatter));

                // On supprime le QRcode une fois terminé
                Path qrToDeletePath = Paths.get(imageUrl);
                if(Files.exists(qrToDeletePath)) {
                    Files.delete(qrToDeletePath);
                }
                return ticketRepository.save(ticketToPdf);
            }
        }
        return null;
    }
}
