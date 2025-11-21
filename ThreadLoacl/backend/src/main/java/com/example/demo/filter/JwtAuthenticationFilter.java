package com.example.demo.filter;

import com.example.demo.service.UserService;
import com.example.demo.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            if (userService.validateToken(token)) {
                String username = userService.getUsernameFromToken(token);
                var user = userService.findByUsername(username);
                user.ifPresent(u -> {
                    System.out.println("----------------------" + token);
                    Object object = UserContext.getUser();
                    if(object!=null){
                        System.out.println(object.toString());
                    }
                    UserContext.setToken(token);
                    UserContext.setUser(u);
                    object = UserContext.getUser();
                    if(object!=null){
                        System.out.println(object.toString());
                    }
                });

            }
        }
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            byte[] body = requestWrapper.getContentAsByteArray();
            String name = new String(body, request.getCharacterEncoding());
            System.out.println("----------------------bodyString" + name);
            String token = UserContext.getToken();
            if(token!=null){
                System.out.println(token.hashCode());
            }
            UserContext.setToken(name);
            token = UserContext.getToken();
            if(token!=null){
                System.out.println(token.hashCode());
            }
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login");
    }
}
