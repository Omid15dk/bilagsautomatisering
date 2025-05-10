package dk.LynRegn;

import dk.LynRegn.logic.CustomerMapper;
import dk.LynRegn.logic.FileSaver;
import dk.LynRegn.logic.MailFetcher;
import dk.LynRegn.model.Attachment;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Load secrets.json directly
            String content = new String(Files.readAllBytes(Paths.get("secrets.json")));
            JSONObject json = new JSONObject(content);
            String email = json.getString("email");
            String password = json.getString("password");

            // Initialize components
            CustomerMapper mapper = new CustomerMapper("customers.csv");
            FileSaver saver = new FileSaver(mapper);
            MailFetcher fetcher = new MailFetcher(email, password);

            // Fetch and print attachments
            List<Attachment> attachments = fetcher.fetchAttachments();
            for (Attachment att : attachments) {
                System.out.println("📎 Found: " + att.getOriginalFilename() + " from " + att.getSenderEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
