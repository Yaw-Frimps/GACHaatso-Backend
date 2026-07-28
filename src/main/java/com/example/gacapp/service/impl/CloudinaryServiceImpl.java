package com.example.gacapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.gacapp.dto.response.CloudinaryResponse;
import com.example.gacapp.model.ImageFolder;
import com.example.gacapp.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryResponse upload(
            MultipartFile file,
            ImageFolder folder,
            String objectId
    ) {

        try {

            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),

                            ObjectUtils.asMap(

                                    "folder",
                                    folder.getFolder(),

                                    "public_id",
                                    objectId + "-" + UUID.randomUUID(),

                                    "overwrite",
                                    true,

                                    "resource_type",
                                    "image"

                            )
                    );

            return CloudinaryResponse.builder()
                    .imageUrl(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();

        } catch (IOException e) {

            throw new RuntimeException("Image upload failed.", e);

        }

    }

    @Override
    public void delete(String publicId) {

        if(publicId == null){

            return;

        }

        try{

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

        }catch(Exception e){

            log.error("Unable to delete image",e);

        }

    }

}