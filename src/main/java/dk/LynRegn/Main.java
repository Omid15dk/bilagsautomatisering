package dk.LynRegn;

import dk.LynRegn.logic.MailFetcher;
import dk.LynRegn.model.Attachment;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            MailFetcher fetcher = new MailFetcher("omid15dk@gmail.com", "guefmhaadkrkdmmd");
            List<Attachment> attachments = fetcher.fetchAttachments();

            for (Attachment att : attachments) {
                System.out.println("📎 Found: " + att.getOriginalFilename() + " from " + att.getSenderEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
