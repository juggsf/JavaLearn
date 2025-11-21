package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final List<User> userList = new ArrayList<>();
    
    @Autowired
    private JwtUtil jwtUtil;

    static {
        userList.add(new User("admin", "admin123", "admin@example.com"));
        userList.add(new User("user", "user123", "user@example.com"));
        for (int i = 0; i <= 100; i++) {
            userList.add(new User(String.valueOf(i), "user123", String.valueOf(i)+"@example.com"));
            
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userList);
    }

    public Optional<User> findByUsername(String username) {
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public boolean validateUser(String username, String password) {
        // Auto-create user if it doesn't exist and username is numeric (for batch testing)
        if (!findByUsername(username).isPresent() && username.matches("\\d+")) {
            synchronized (userList) {
                if (!findByUsername(username).isPresent()) {
                    addUser(new User(username, username, username + "@test.com"));
                }
            }
        }
        
        return findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }
    
    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }
    
    public boolean validateToken(String token) {
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            return findByUsername(username).isPresent();
        }
        return false;
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public void addUser(User user) {
        userList.add(user);
    }
    
    public int getUserCount() {
        return userList.size();
    }
}
