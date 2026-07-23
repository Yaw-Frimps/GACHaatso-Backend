package com.example.gacapp.util;

import com.example.gacapp.exception.FailedToStoreFileException;
import com.example.gacapp.exception.MaximumFileSizeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path baseLocation = Paths.get("uploads").toAbsolutePath().normalize();

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    @Override
    public String storeFile(
            MultipartFile file,
            String subFolder,
            String id,
            String oldFileName
    ) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WEBP images are allowed.");
        }

        // 2. Validate file size against custom limit
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaximumFileSizeException("File size exceeds maximum permitted limit of 10MB.");
        }

        // 3. Validate file extension
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);

        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("File must have a valid extension.");
        }

        extension = extension.toLowerCase();
        String filename = "img-" + UUID.randomUUID() + "." + extension;

        try {
            Path targetFolder = baseLocation.resolve(subFolder).resolve(id);
            Files.createDirectories(targetFolder);

            // Delete existing file if replacing during update
            deleteOldFileQuietly(targetFolder, oldFileName);

            // Read original image
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new IllegalArgumentException("The uploaded file is not a valid or readable image.");
            }

            // Resize and optimize image
            BufferedImage resized = Thumbnails.of(original)
                    .size(1080, 1080)
                    .keepAspectRatio(true)
                    .asBufferedImage();

            Path target = targetFolder.resolve(filename);

            // Normalize 'jpg' -> 'jpeg' for standard ImageIO format writers
            String formatName = extension.equalsIgnoreCase("jpg") ? "jpeg" : extension;
            boolean written = ImageIO.write(resized, formatName, target.toFile());

            if (!written) {
                log.error("ImageIO failed to find an appropriate writer for format: {}", formatName);
                throw new FailedToStoreFileException("Failed to encode image format: " + extension);
            }

            return filename;

        } catch (IOException e) {
            log.error("Failed to store file for entity id={}", id, e);
            throw new FailedToStoreFileException("File storage operation failed on the server.");
        }
    }

    public String getFileUrl(String subFolder, String id, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        return "/uploads/" + subFolder + "/" + id + "/" + fileName;
    }

    public void deleteFileDirectory(String subFolder, String id) {
        try {
            Path targetFolder = baseLocation.resolve(subFolder).resolve(id);

            if (Files.exists(targetFolder)) {
                // Try-with-resources prevents file handle leaks during directory walking
                try (Stream<Path> pathStream = Files.walk(targetFolder)) {
                    pathStream
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    log.warn("Failed to delete file/folder path: {}", path, e);
                                }
                            });
                }
                log.info("Deleted directory for {}: {}", subFolder, id);
            }

        } catch (IOException e) {
            log.error("Failed to walk/delete directory for {}:{}", subFolder, id, e);
        }
    }

    private void deleteOldFileQuietly(Path targetFolder, String oldFileName) {
        if (oldFileName == null || oldFileName.isBlank()) {
            return;
        }

        Path oldFilePath = targetFolder.resolve(oldFileName);
        try {
            Files.deleteIfExists(oldFilePath);
        } catch (IOException e) {
            log.warn("Could not delete old image file: {}", oldFilePath, e);
        }
    }
}