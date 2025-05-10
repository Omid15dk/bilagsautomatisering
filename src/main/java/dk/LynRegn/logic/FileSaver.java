package dk.LynRegn.logic;

import dk.LynRegn.model.Attachment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;

public class FileSaver {

    private final CustomerMapper customerMapper;

    public FileSaver(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public void save(Attachment attachment) {
        try {
            String customerFolder = customerMapper.getCustomerFolder(attachment.getSenderEmail());
            if (customerFolder == null) {
                throw new IllegalArgumentException("Unknown customer email: " + attachment.getSenderEmail());
            }

            String year = String.valueOf(Year.now().getValue());
            Path fullPath = Paths.get("kunder", customerFolder, year, "bilagsopbevaring");
            Files.createDirectories(fullPath);

            File bilagDir = fullPath.toFile();
            String[] existingFiles = bilagDir.list((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            int nextNumber = (existingFiles != null ? existingFiles.length : 0) + 1;

            String filename = nextNumber + "_" + attachment.getOriginalFilename();
            Path filePath = fullPath.resolve(filename);

            try (InputStream in = attachment.getContent();
                 OutputStream out = new FileOutputStream(filePath.toFile())) {
                in.transferTo(out);
            }

            System.out.println("✅ Saved: " + filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save attachment", e);
        }
    }

}
