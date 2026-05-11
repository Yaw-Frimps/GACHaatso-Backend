package com.example.gacapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class GacAppProperties {
    private DefaultAdmin defaultAdmin;

    @Data
    public static class DefaultAdmin {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }
}
