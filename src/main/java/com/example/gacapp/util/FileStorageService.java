package com.example.gacapp.util;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(
            MultipartFile file,
            String subFolder,
            String id,
            String oldFileName
    );

    String getFileUrl(
            String subFolder,
            String id,
            String fileName
    );

    void deleteFileDirectory(
            String subFolder,
            String id
    );
}
