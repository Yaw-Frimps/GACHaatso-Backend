package com.example.gacapp.controller;

import com.example.gacapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public String getAllUsers() {
        // This is a placeholder. In a real application, you would return a list of users.
        return "This endpoint will return all users. Only accessible by ADMIN role.";
    }
}
