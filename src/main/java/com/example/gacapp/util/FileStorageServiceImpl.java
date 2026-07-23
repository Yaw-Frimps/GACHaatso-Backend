package com.example.gacapp.util;

import com.example.gacapp.exception.FailedToStoreFileException;
import com.example.gacapp.exception.MaximumFileSizeException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;


@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {


    private final Path baseLocation =
            Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();


    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024L; // 10MB


    private static final List<String> ALLOWED_TYPES =
            List.of(
                    "image/jpeg",
                    "image/png",
                    "image/gif",
                    "image/webp"
            );


    private static final List<String> ALLOWED_EXTENSIONS =
            List.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "gif",
                    "webp"
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


        validateFile(file);


        String extension =
                Objects.requireNonNull(FilenameUtils.getExtension(
                        file.getOriginalFilename()
                )).toLowerCase();


        String filename =
                "img-" + UUID.randomUUID()
                        + "." + extension;


        try {

            Path targetFolder =
                    baseLocation
                            .resolve(subFolder)
                            .resolve(id);


            Files.createDirectories(targetFolder);



            // Delete previous image during update
            deleteOldFileQuietly(
                    targetFolder,
                    oldFileName
            );



            BufferedImage original =
                    ImageIO.read(
                            file.getInputStream()
                    );


            if (original == null) {

                throw new IllegalArgumentException(
                        "Uploaded file is not a valid image"
                );
            }



            BufferedImage resized =
                    Thumbnails.of(original)
                            .size(1080,1080)
                            .keepAspectRatio(true)
                            .asBufferedImage();



            Path target =
                    targetFolder.resolve(filename);



            String format =
                    extension.equals("jpg")
                            ? "jpeg"
                            : extension;



            boolean written =
                    ImageIO.write(
                            resized,
                            format,
                            target.toFile()
                    );


            if (!written) {

                throw new FailedToStoreFileException(
                        "Unable to encode image"
                );
            }


            log.info(
                    "File stored successfully: {}",
                    target
            );


            return filename;


        } catch (IOException e) {

            log.error(
                    "File storage failed",
                    e
            );


            throw new FailedToStoreFileException(
                    "Unable to store file"
            );
        }
    }





    private void validateFile(MultipartFile file) {


        if (file.getSize() > MAX_FILE_SIZE) {

            throw new MaximumFileSizeException(
                    "File size cannot exceed 10MB"
            );
        }



        String contentType =
                file.getContentType();



        if (contentType == null ||
                !ALLOWED_TYPES.contains(
                        contentType.toLowerCase()
                )) {


            throw new IllegalArgumentException(
                    "Only JPG, PNG, GIF and WEBP images are allowed"
            );
        }



        String extension =
                FilenameUtils.getExtension(
                        file.getOriginalFilename()
                );



        if (extension == null ||
                extension.isBlank()) {

            throw new IllegalArgumentException(
                    "File must have an extension"
            );
        }



        if (!ALLOWED_EXTENSIONS.contains(
                extension.toLowerCase()
        )) {


            throw new IllegalArgumentException(
                    "Unsupported image extension"
            );
        }
    }





    @Override
    public String getFileUrl(
            String subFolder,
            String id,
            String fileName
    ) {


        if(fileName == null || fileName.isBlank()){
            return null;
        }


        return "/uploads/"
                + subFolder
                + "/"
                + id
                + "/"
                + fileName;
    }





    @Override
    public void deleteFileDirectory(
            String subFolder,
            String id
    ) {


        try {


            Path targetFolder =
                    baseLocation
                            .resolve(subFolder)
                            .resolve(id);



            if(Files.exists(targetFolder)){


                try(Stream<Path> paths =
                            Files.walk(targetFolder)){


                    paths.sorted(
                                    Comparator.reverseOrder()
                            )
                            .forEach(path -> {

                                try {

                                    Files.deleteIfExists(path);

                                } catch(IOException e){

                                    log.warn(
                                            "Unable to delete {}",
                                            path
                                    );
                                }

                            });

                }


                log.info(
                        "Deleted directory {}",
                        targetFolder
                );
            }



        }catch(IOException e){

            log.error(
                    "Failed deleting directory",
                    e
            );
        }
    }



    private void deleteOldFileQuietly(
            Path folder,
            String oldFileName
    ){


        if(oldFileName == null ||
                oldFileName.isBlank()){

            return;
        }



        try {


            Files.deleteIfExists(
                    folder.resolve(oldFileName)
            );


        }catch(IOException e){

            log.warn(
                    "Unable to delete old image {}",
                    oldFileName
            );
        }

    }

}