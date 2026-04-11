package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.AuthService;

/**
 * Controller responsible for authentication-related operations.
 * <p>
 * This includes:
 * <ul>
 *   <li>User login and JWT token generation</li>
 *   <li>Public user signup (pending admin approval)</li>
 * </ul>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs an AuthController with required dependencies.
     *
     * @param authService service handling authentication logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Request body for login.
     */
    public static class LoginRequest {
        private String username;
        private String password;

        /**
         * @return the username provided by the user
         */
        public String getUsername() {
            return username;
        }

        /**
         * @return the password provided by the user
         */
        public String getPassword() {
            return password;
        }
    }

    /**
     * Response body for login.
     */
    public static class LoginResponse {
        private String token;

        /**
         * Constructs a LoginResponse.
         *
         * @param token JWT token issued upon successful authentication
         */
        public LoginResponse(String token) {
            this.token = token;
        }

        /**
         * @return the JWT token
         */
        public String getToken() {
            return token;
        }
    }

    /**
     * Request body for user signup.
     */
    public static class SignupRequest {
        private String username;
        private String password;
        private String email;

        /**
         * @return the username requested by the user
         */
        public String getUsername() {
            return username;
        }

        /**
         * @return the password requested by the user
         */
        public String getPassword() {
            return password;
        }

        /**
         * @return the email address of the user
         */
        public String getEmail() {
            return email;
        }
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param request contains username and password
     * @return LoginResponse containing JWT token
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return new LoginResponse(token);
    }

    /**
     * Registers a new user in a pending state.
     * <p>
     * The created user will:
     * <ul>
     *   <li>Have role PARTICIPANT</li>
     *   <li>Be disabled until approved by an administrator</li>
     * </ul>
     *
     * @param request contains username, email, and password
     * @return the created User entity (pending approval)
     */
    @PostMapping("/signup")
    public User signup(@RequestBody SignupRequest request) {
        return authService.signup(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
    }
}