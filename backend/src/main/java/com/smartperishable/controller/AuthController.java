package com.smartperishable.controller;

import com.smartperishable.model.User;
import com.smartperishable.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
        if (user.isPresent()) {
            User u = user.get();
            u.setLastLogin(LocalDateTime.now());
            userRepository.save(u);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", u.getId());
            response.put("username", u.getUsername());
            response.put("fullName", u.getFullName());
            response.put("role", u.getRole().name());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Invalid username or password");
        return ResponseEntity.status(401).body(error);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Username already exists"));
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "userId", saved.getId()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
