package com.setupbuilder.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.avatar-max-size}")
    private long avatarMaxSize;

    @Value("${app.upload.banner-max-size}")
    private long bannerMaxSize;

    private Path root;

    @PostConstruct
    public void init() {
        try {
            this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(root.resolve("avatars"));
            Files.createDirectories(root.resolve("banners"));
            log.info("File storage root: {}", root);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize upload directory", e);
        }
    }

    /**
     * Saves an avatar. Returns the public URL path (e.g. "/uploads/avatars/uuid.jpg").
     */
    public String saveAvatar(MultipartFile file) {
        return save(file, "avatars", avatarMaxSize);
    }

    /**
     * Saves a banner. Returns the public URL path.
     */
    public String saveBanner(MultipartFile file) {
        return save(file, "banners", bannerMaxSize);
    }

    /**
     * Deletes a file given its public URL path. Safe to call with null or missing files.
     */
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        // Only delete files inside our own uploads directory
        if (!url.startsWith("/uploads/")) return;

        try {
            String relative = url.substring("/uploads/".length());
            Path target = root.resolve(relative).normalize();
            if (!target.startsWith(root)) return; // safety: don't escape
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Failed to delete old file: {}", url, e);
        }
    }

    private String save(MultipartFile file, String subdir, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("File too large. Max %d MB.", maxSize / (1024 * 1024))
            );
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Unsupported format. Allowed: JPG, PNG, WEBP, GIF."
            );
        }

        // Sanitize extension from the original filename
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot).toLowerCase();
            // Whitelist extensions to avoid tricky filenames
            if (!ext.matches("\\.(jpg|jpeg|png|webp|gif)")) ext = ".jpg";
        } else {
            ext = ".jpg";
        }

        String filename = UUID.randomUUID() + ext;
        Path target = root.resolve(subdir).resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return "/uploads/" + subdir + "/" + filename;
    }
}