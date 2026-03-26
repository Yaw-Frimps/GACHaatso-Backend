package com.example.gacapp.controller;

import com.example.gacapp.dto.request.LoginRequest;
import com.example.gacapp.dto.request.RegisterRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.LoginResponse;
import com.example.gacapp.dto.response.RegisterResponse;
import com.example.gacapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@RequestBody RegisterRequest request){
        RegisterResponse response = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User logged in successfully"));
    }

}
