package com.example.gacapp.service;

import com.example.gacapp.dto.request.LoginRequest;
import com.example.gacapp.dto.request.RegisterRequest;
import com.example.gacapp.dto.response.LoginResponse;
import com.example.gacapp.dto.response.RegisterResponse;

public interface UserService {
        RegisterResponse registerUser(RegisterRequest request);
        LoginResponse loginUser(LoginRequest request);
}
