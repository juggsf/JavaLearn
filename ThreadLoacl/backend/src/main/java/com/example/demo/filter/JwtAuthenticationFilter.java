package com.example.demo.filter;

import com.example.demo.model.test;
import com.example.demo.service.UserService;
import com.example.demo.util.UserContext;
import com.example.demo.util.testContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

        String foo = request.getParameter("foo");
        if(foo!= null){
            test OldFoo = testContext.getTest();
            if (OldFoo != null) {
                System.out.println("旧的foo：" + OldFoo.getFoo() + " 哈希code:" + OldFoo.hashCode());
            }
            else{
                System.out.println("没有旧的foo");
            }
            testContext.setTest(new test(Integer.parseInt(foo)));
            System.out.println("新的foo：" + testContext.getTest().getFoo() + " 哈希code:" +testContext.getTest().hashCode());
            System.gc();
            
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login");
    }
}
