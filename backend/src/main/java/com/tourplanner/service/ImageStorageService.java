package com.tourplanner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.tourplanner.exception.InvalidImageException;

// Speichert Bilder sicher; Dateiendung aus Content-Type (Whitelist), nie vom Client-Filename.
@Service
public class ImageStorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.image.base-dir}")
    private String baseDir;

    public String saveImage(MultipartFile file) throws IOException {
        String ext = resolveExtension(file.getContentType());

        if (file.getSize() > MAX_SIZE) {
            throw new InvalidImageException("Datei zu groß (max. 5 MB)");
        }

        String filename = UUID.randomUUID() + ext;
        Path dir = Paths.get(baseDir);
        Files.createDirectories(dir);
        Path target = dir.resolve(filename).normalize();
        if (!target.startsWith(dir.normalize())) {
            throw new InvalidImageException("Ungültiger Dateiname");
        }

        Files.copy(file.getInputStream(), target);
        return "/api/images/" + filename;
    }

    // Leitet die Dateiendung aus dem Content-Type ab – wirft InvalidImageException für unbekannte Typen.
    private String resolveExtension(String contentType) {
        if (contentType == null) {
            throw new InvalidImageException("Fehlender Content-Type");
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new InvalidImageException("Ungültiger Dateityp: " + contentType);
        };
    }
}