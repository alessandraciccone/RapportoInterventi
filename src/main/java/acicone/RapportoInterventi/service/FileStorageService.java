package acicone.RapportoInterventi.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String BASE_PATH = "uploads/reports/";

    public String saveBase64(String base64) {
        try {
            byte[] fileBytes = Base64.getDecoder().decode(base64);

            String fileName = UUID.randomUUID().toString();
            Path path = Paths.get(BASE_PATH + fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, fileBytes);

            return "/files/reports/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Errore nel salvataggio del file", e);
        }
    }
}
