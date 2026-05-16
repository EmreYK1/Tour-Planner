package com.tourplanner.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.tourplanner.exception.InvalidImageException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    @Value("${app.image.base-dir}")
    private String baseDir;

    public String saveImage(MultipartFile file) throws IOException {

        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidImageException("Ungültiger Dateityp: " + contentType);
        }
        
        if (file.getSize() > MAX_SIZE) {
            throw new InvalidImageException("Datei zu groß (max. 5MB)");
        }
        
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        String filename = UUID.randomUUID().toString() + ext;

        Path dir = Paths.get(baseDir);
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);

        Files.copy(file.getInputStream(), target);
        
        return "/api/images/" + filename;
    }
}