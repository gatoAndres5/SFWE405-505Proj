package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;

/**
 * Controller responsible for administrative user management.
 * <p>
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a UserController with required dependencies.
     *
     * @param userService service handling user management operations
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Request body for creating a new user by an admin.
     */
    public static class CreateUserRequest {
        public String username;
        public String email;
        public String password;
        public UserRole role;
    }

    /**
     * Creates a new user with a specified role.
     *
     * @param request contains username, email, password, and role
     * @return the created User
     */
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.username,
                request.email,
                request.password,
                request.role
        );
    }

    /**
     * Retrieves all users in the system.
     *
     * @return list of all users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id user ID
     * @return the User if found
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Retrieves all users who are pending approval.
     *
     * @return list of users with enabled = false
     */
    @GetMapping("/pending")
    public List<User> getPendingUsers() {
        return userService.getPendingUsers();
    }

    /**
     * Approves a user by enabling their account.
     *
     * @param id user ID
     * @return the updated User
     */
    @PatchMapping("/{id}/approve")
    public User approveUser(@PathVariable Long id) {
        return userService.approveUser(id);
    }

    /**
     * Request body for changing a user's role.
     */
    public static class ChangeRoleRequest {
        public String role;
    }

    /**
     * Updates the role of a specific user.
     *
     * @param id user ID
     * @param request contains the new role
     * @return updated User
     */
    @PatchMapping("/{id}/role")
    public User changeUserRole(@PathVariable Long id,
                              @RequestBody ChangeRoleRequest request) {
        return userService.changeUserRole(id, request.role);
    }

    /**
     * Disables a user account.
     *
     * @param id user ID
     * @return updated User with enabled = false
     */
    @PatchMapping("/{id}/disable")
    public User disableUser(@PathVariable Long id) {
        return userService.disableUser(id);
    }
}