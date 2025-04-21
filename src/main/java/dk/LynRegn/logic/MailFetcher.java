package dk.LynRegn.logic;

import dk.LynRegn.model.Attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.mail.*;

public class MailFetcher {
    private final String email;
    private final String password;

    public MailFetcher(String email, String password) throws MessagingException {
        this.email = email;
        this.password = password;
    }

    public List<Attachment> fetchAttachments() throws MessagingException, IOException {
        Store store = getImapStore();
        Folder folder = getFolderFromStore(store, "INBOX");
        List<Attachment> attachments = new ArrayList<>();

        int total = folder.getMessageCount();
        int start = Math.max(1, total - 19);
        Message[] messages = folder.getMessages(start, total);

        for(Message message : messages) {
            if (message.isMimeType("multipart/*")) {
                Object content = message.getContent();
                if (content instanceof Multipart multipart) {
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);

                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String filename = part.getFileName();
                            InputStream stream = part.getInputStream();
                            String sender = message.getFrom()[0].toString();

                            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

                            if (extension.equals("pdf") || extension.equals("png") || extension.equals("jpg")) {
                                Attachment attachment = new Attachment(filename, stream, sender, extension);
                                attachments.add(attachment);
                            }
                        }
                    }
                }
            }
        }
        return attachments;

    }

    private Folder getFolderFromStore(Store store, String folderName) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private Store getImapStore() throws MessagingException {
        Session session = Session.getInstance(getImapProperties());
        Store store = session.getStore("imaps");
        store.connect(email, password);
        return store;
    }

    private Properties getImapProperties() {
        Properties props = new Properties();
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.ssl.trust", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.protocols", "TLSv1.2");
        return props;
    }

}
