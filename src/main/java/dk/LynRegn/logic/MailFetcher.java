package dk.LynRegn.logic;

import dk.LynRegn.model.Attachment;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MailFetcher {

    private final String email;
    private final String password;

    public MailFetcher(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public List<Attachment> fetchAttachments() throws MessagingException, IOException {
        List<Attachment> attachments = new ArrayList<>();

        Store store = getImapStore();
        Folder folder = getFolderFromStore(store, "INBOX");
        Message[] messages = folder.getMessages(Math.max(1, folder.getMessageCount() - 20 + 1), folder.getMessageCount());

        for (Message message : messages) {
            Address[] fromAddresses = message.getFrom();
            String senderEmail = fromAddresses.length > 0 ? fromAddresses[0].toString() : "unknown";

            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();

                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    String fileName = bodyPart.getFileName();
                    if (fileName != null && bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                        String lowerName = fileName.toLowerCase();
                        if (lowerName.endsWith(".pdf") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                            InputStream is = bodyPart.getInputStream();
                            byte[] content = is.readAllBytes();
                            attachments.add(new Attachment(fileName, new ByteArrayInputStream(content), senderEmail, getFileExtension(lowerName)));
                        }
                    }
                }
            }
        }

        folder.close(false);
        store.close();
        return attachments;
    }

    private Store getImapStore() throws MessagingException {
        Session session = Session.getInstance(getImapProperties());
        Store store = session.getStore("imaps");
        store.connect(email, password);
        return store;
    }

    private Folder getFolderFromStore(Store store, String folderName) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private Properties getImapProperties() {
        Properties props = new Properties();
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.trust", "imap.gmail.com");
        return props;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot != -1 ? fileName.substring(lastDot + 1) : "";
    }
}
