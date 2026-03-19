package com.example.gacapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GacappApplication {

    public static void main(String[] args) {
        SpringApplication.run(GacappApplication.class, args);
    }

}
