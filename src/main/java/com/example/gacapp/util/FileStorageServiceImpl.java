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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path baseLocation =
            Paths.get("uploads").toAbsolutePath().normalize();

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

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

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaximumFileSizeException("File too large");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Invalid extension");
        }

        String filename = "img-" + UUID.randomUUID() + "." + extension;

        try {
            Path targetFolder = baseLocation.resolve(subFolder).resolve(id);
            Files.createDirectories(targetFolder);

            if (oldFileName != null && !oldFileName.isBlank()) {
                Files.deleteIfExists(targetFolder.resolve(oldFileName));
            }

            BufferedImage original = ImageIO.read(file.getInputStream());

            if (original == null) {
                throw new IllegalArgumentException("Invalid image");
            }

            BufferedImage resized = Thumbnails.of(original)
                    .size(1080, 1080)
                    .keepAspectRatio(true)
                    .asBufferedImage();

            Path target = targetFolder.resolve(filename);

            ImageIO.write(resized, extension, target.toFile());

            return filename;

        } catch (IOException e) {
            throw new FailedToStoreFileException("File storage failed", e);
        }
    }

    // ✅ NEW: Align with EventService pattern
    public String getFileUrl(String subFolder, String id, String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        return "/uploads/" + subFolder + "/" + id + "/" + fileName;
    }



    public void deleteFileDirectory(String subFolder, String id) {
        try {
            Path targetFolder = baseLocation.resolve(subFolder).resolve(id);

            if (Files.exists(targetFolder)) {
                Files.walk(targetFolder)
                        .sorted((a, b) -> b.compareTo(a)) // delete files before folder
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete file: {}", path);
                            }
                        });

                log.info("Deleted file directory: {}", targetFolder);
            }

        } catch (IOException e) {
            log.error("Failed to delete directory for {}:{}", subFolder, id, e);
        }
    }
}