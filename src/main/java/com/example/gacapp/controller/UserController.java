package com.example.gacapp.controller;

import com.example.gacapp.dto.request.ForgotPasswordRequest;
import com.example.gacapp.dto.request.LoginRequest;
import com.example.gacapp.dto.request.RegisterRequest;
import com.example.gacapp.dto.request.ResetPasswordRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.LoginResponse;
import com.example.gacapp.dto.response.RegisterResponse;
import com.example.gacapp.service.PasswordResetService;
import com.example.gacapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs for managing user authentication"
)
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(
            summary = "User Registration",
            description = "Allows user to create an account on the platform"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@RequestBody RegisterRequest request){
        RegisterResponse response = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Allows users to login into the platform"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Member created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User logged in successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot Password",
            description = "Allows the user to forget password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Password reset email sent if the email exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset email sent if the email exists"));
    }

     @PostMapping("/reset-password")
     @Operation(
             summary = "Reset Password",
             description = "Allows the user to reset password"
     )
     @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Password reset successfully"),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
     })
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }


}
