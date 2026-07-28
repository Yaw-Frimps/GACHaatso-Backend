package com.example.gacapp.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryResponse {

    private String imageUrl;

    private String publicId;

}
