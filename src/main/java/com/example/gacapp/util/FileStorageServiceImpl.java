package com.example.gacapp.util;

import com.example.gacapp.exception.FailedToStoreFileException;
import com.example.gacapp.exception.MaximumFileSizeException;
import lombok.RequiredArgsConstructor;
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
public class FileStorageServiceImpl implements FileStorageService {

    private final Path baseLocation =
            Paths.get("uploads").toAbsolutePath().normalize();

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024L;

    public String storeFile(
            MultipartFile file,
            String subFolder,
            String id,
            String oldFileName
    ) {

        List<String> allowedTypes = List.of(
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp"
        );

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Invalid file type. Only JPG, PNG, GIF, WebP allowed."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaximumFileSizeException(
                    "File size exceeds maximum of 10MB."
            );
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("File extension is invalid");
        }

        String filename = "img-" + UUID.randomUUID() + "." + extension;

        try {

            Path targetFolder = baseLocation.resolve(subFolder).resolve(id);

            Files.createDirectories(targetFolder);

            if (oldFileName != null && !oldFileName.isBlank()) {

                Path oldFilePath = targetFolder.resolve(oldFileName);

                Files.deleteIfExists(oldFilePath);
            }

            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            BufferedImage resizedImage = Thumbnails.of(originalImage)
                            .size(1080, 1080)
                            .keepAspectRatio(true)
                            .asBufferedImage();

            Path targetLocation = targetFolder.resolve(filename);

            ImageIO.write(resizedImage, extension, targetLocation.toFile());

            return filename;

        } catch (IOException e) {

            throw new FailedToStoreFileException("Failed to store file", e);
        }
    }

    public String getFileUrl(
            String subFolder,
            String id,
            String filename
    ) {

        return "/uploads/"
                + subFolder
                + "/"
                + id
                + "/"
                + filename;
    }
}