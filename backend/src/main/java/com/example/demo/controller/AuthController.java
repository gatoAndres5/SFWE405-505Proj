package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class LoginResponse {
        private String token;

        public LoginResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String role;
        private String email;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail(){
            return email;
        }

        public String getRole() {
            return role;
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return new LoginResponse(token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }
}