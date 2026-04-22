package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;

/**
 * Controller responsible for user management.
 *
 * Administrative endpoints require ADMIN role.
 * Event assignment visibility may also be accessed by ORGANIZER and STAFF
 * for retrieving a user's assigned events.
 */
@RestController
@RequestMapping("/users")
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
        public Long participantId;
    }

    /**
     * Creates a new user with a specified role.
     *
     * Allowed Roles: ADMIN
     *
     * @param request contains username, email, password, role, and optional participantId
     * @return the created User
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.username,
                request.email,
                request.password,
                request.role,
                request.participantId
        );
    }

    /**
     * Retrieves all users in the system.
     *
     * Allowed Roles: ADMIN
     *
     * @return list of all users
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a specific user by ID.
     *
     * Allowed Roles: ADMIN
     *
     * @param id user ID
     * @return the User if found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Retrieves all users who are pending approval.
     *
     * Allowed Roles: ADMIN
     *
     * @return list of users with enabled = false
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<User> getPendingUsers() {
        return userService.getPendingUsers();
    }

    /**
     * Approves a user by enabling their account.
     *
     * Allowed Roles: ADMIN
     *
     * @param id user ID
     * @return the updated User
     */
    @PreAuthorize("hasRole('ADMIN')")
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
     * Allowed Roles: ADMIN
     *
     * @param id user ID
     * @param request contains the new role
     * @return updated User
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public User changeUserRole(@PathVariable Long id,
                               @RequestBody ChangeRoleRequest request) {
        return userService.changeUserRole(id, request.role);
    }

    /**
     * Disables a user account.
     *
     * Allowed Roles: ADMIN
     *
     * @param id user ID
     * @return updated User with enabled = false
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public User disableUser(@PathVariable Long id) {
        return userService.disableUser(id);
    }

    /**
     * Retrieves the events assigned to a specific user.
     *
     * Allowed Roles: ADMIN, ORGANIZER, STAFF
     *
     * @param id user ID
     * @return list of events assigned to the user
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF')")
    @GetMapping("/{id}/events")
    public List<Event> getUserEvents(@PathVariable Long id) {
        return userService.getAssignedEvents(id);
    }
    /**
     * Retrieves the events assigned to the currently authenticated user.
     *
     * Allowed Roles: ADMIN, ORGANIZER, STAFF
     *
     * @param authentication injected by Spring Security — contains the logged-in username
     * @return list of events assigned to the current user
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF')")
    @GetMapping("/me/events")
    public List<Event> getMyEvents(Authentication authentication) {
        return userService.getAssignedEventsByUsername(authentication.getName());
    }
}