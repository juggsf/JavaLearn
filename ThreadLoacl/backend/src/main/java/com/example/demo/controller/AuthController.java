package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String token = userService.generateToken(username);
        Optional<User> user = userService.findByUsername(username);
        
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", user.get());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (userService.validateToken(token)) {
                return ResponseEntity.ok(userService.getAllUsers());
            }
        }
        return ResponseEntity.status(401).build();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (userService.validateToken(token)) {
                String username = userService.getUsernameFromToken(token);
                Optional<User> user = userService.findByUsername(username);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user", user.get());
                response.put("token", token);
                
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(401).build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("threadLocalUser", UserContext.getCurrentUsername());
        stats.put("threadLocalToken", UserContext.getToken() != null ? "Present" : "Not Present");
        
        return ResponseEntity.ok(stats);
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
