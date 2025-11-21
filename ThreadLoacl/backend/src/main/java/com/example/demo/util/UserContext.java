package com.example.demo.util;

import com.example.demo.model.User;

public class UserContext {
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    
    public static void setToken(String token) {
        currentToken.set(token);
    }
    
    public static String getToken() {
        return currentToken.get();
    }
    
    public static void setUser(User user) {
        currentUser.set(user);
    }
    
    public static User getUser() {
        return currentUser.get();
    }
    
    public static String getCurrentUsername() {
        User user = currentUser.get();
        return user != null ? user.getUsername() : null;
    }
    
    public static void clear() {
        currentToken.remove();
        currentUser.remove();
    }
}
