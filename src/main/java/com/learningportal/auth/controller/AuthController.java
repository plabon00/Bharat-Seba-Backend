package com.learningportal.auth.controller;

import com.learningportal.auth.dto.FirebaseLoginRequest;
import com.learningportal.auth.dto.JwtResponse;
import com.learningportal.auth.dto.LoginRequest;
import com.learningportal.auth.dto.RegisterRequest;
import com.learningportal.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/check-email")
    public ResponseEntity<com.learningportal.auth.dto.CheckEmailResponse> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.checkEmail(email));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody com.learningportal.auth.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody com.learningportal.auth.dto.ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/firebase-login")
    public ResponseEntity<JwtResponse> firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
        JwtResponse jwtResponse = authService.firebaseLogin(request);
        return ResponseEntity.ok(jwtResponse);
    }
}
