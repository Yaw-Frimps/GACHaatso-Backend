package com.example.gacapp.service;

import com.example.gacapp.dto.response.CloudinaryResponse;
import com.example.gacapp.model.ImageFolder;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    CloudinaryResponse upload(
            MultipartFile file,
            ImageFolder folder,
            String objectId
    );

    void delete(String publicId);

}
