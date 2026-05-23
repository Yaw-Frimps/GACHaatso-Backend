package com.example.gacapp.util;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subFolder, String id, String oldFileName);
}
