package dk.LynRegn.logic;

import dk.LynRegn.model.Attachment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AttachmentProcessor {

    public AttachmentProcessor(){

    }

    public List<Attachment> processAttachments(List<Attachment> attachments){
        List<Attachment> processedAttachments = new LinkedList<>();
        for(Attachment att : attachments){
            if(!att.getFileExtension().toLowerCase(Locale.ROOT).equals("pdf")){
                att = convertToPdf(att);
            }
            processedAttachments.add(att);
        }
        return processedAttachments;
    }

    private Attachment convertToPdf(Attachment nonPdf) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            byte[] imageBytes = nonPdf.getContent().readAllBytes();
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, nonPdf.getOriginalFilename());

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 100, 300, 400, 400);
            }

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            document.save(pdfOutputStream);
            document.close();

            return new Attachment(
                    nonPdf.getOriginalFilename().replaceAll("\\.(jpg|jpeg|png)", ".pdf"),
                    new ByteArrayInputStream(pdfOutputStream.toByteArray()),
                    nonPdf.getSenderEmail(),
                    "pdf"
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to PDF", e);
        }
    }

}
