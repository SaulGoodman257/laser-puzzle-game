package com.puzzle.controller;

import com.puzzle.dto.LoginRequest;
import com.puzzle.dto.RegistrationRequest;
import com.puzzle.model.User;
import com.puzzle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        System.out.println("Received registration request: " + registrationRequest);
        try {
            User registeredUser = userService.registerUser(registrationRequest);
            return ResponseEntity.ok("User registered successfully. User ID: " + registeredUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("Received login request: " + loginRequest);
        try {
            User user = userService.findByUsername(loginRequest.getUsername());
            if (userService.checkPassword(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.ok(user.getUsername());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

