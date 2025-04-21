package dk.LynRegn.model;

import java.io.InputStream;

public class Attachment {
    private final String originalFilename;
    private final InputStream content;
    private final String senderEmail;
    private final String fileExtension;

    public Attachment(String originalFilename, InputStream content, String senderEmail, String fileExtension) {
        this.originalFilename = originalFilename;
        this.content = content;
        this.senderEmail = senderEmail;
        this.fileExtension = fileExtension;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public InputStream getContent() {
        return content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
