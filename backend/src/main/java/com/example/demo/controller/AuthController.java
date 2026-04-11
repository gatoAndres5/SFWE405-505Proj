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
 *   <li>Password reset request handling</li>
 *   <li>Password reset completion</li>
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
     * Request body for forgot-password requests.
     */
    public static class ForgotPasswordRequest {
        private String email;

        /**
         * @return the email address associated with the account
         */
        public String getEmail() {
            return email;
        }
    }

    /**
     * Request body for password reset submission.
     */
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        /**
         * @return the password reset token
         */
        public String getToken() {
            return token;
        }

        /**
         * @return the new password to set
         */
        public String getNewPassword() {
            return newPassword;
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

    /**
     * Initiates a password reset request.
     * <p>
     * If an account exists for the supplied email, a password reset link will be sent.
     * The response is intentionally generic to avoid revealing whether an email address
     * exists in the system.
     *
     * @param request contains the user's email address
     * @return confirmation message
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return "If an account exists for that email, a reset link has been sent.";
    }

    /**
     * Completes a password reset using a valid reset token.
     *
     * @param request contains the reset token and new password
     * @return confirmation message
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return "Password has been reset successfully.";
    }
}