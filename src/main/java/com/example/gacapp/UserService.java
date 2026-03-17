package com.example.gacapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
        public String getUserGreeting() {
            log.info("Generating user greeting");
            return "Hello, User!";
        }
}
