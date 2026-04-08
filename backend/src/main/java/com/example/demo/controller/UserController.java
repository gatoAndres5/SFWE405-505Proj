package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public static class CreateUserRequest {
        public String username;
        public String email;
        public String password;
        public UserRole role;
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.username,
                request.email,
                request.password,
                request.role
        );
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
