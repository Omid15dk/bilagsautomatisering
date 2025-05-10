package dk.LynRegn.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerMapper {

    private final Map<String, String> emailToFolderMap;

    public CustomerMapper(String csvFilePath) throws IOException{
        emailToFolderMap = new HashMap<>();
        loadMapping(csvFilePath);
    }

    private void loadMapping(String csvFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String email = parts[0].trim().toLowerCase();
                    String folder = parts[1].trim();
                    emailToFolderMap.put(email, folder);
                }
            }
        }
    }

    public String getCustomerFolder(String email) {
        return emailToFolderMap.get(email.toLowerCase());
    }
}
