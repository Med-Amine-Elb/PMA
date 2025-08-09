package com.telephonemanager.security;

import com.telephonemanager.entity.User;
import com.telephonemanager.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request, 
                                  @org.springframework.lang.NonNull HttpServletResponse response, 
                                  @org.springframework.lang.NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("=== JWT FILTER: Processing request ===");
        System.out.println("Method: " + method);
        System.out.println("URI: " + requestURI);
        
        // Skip JWT processing for login endpoint, test endpoints, and WebSocket endpoints
        if (requestURI.equals("/auth/login") || 
            requestURI.startsWith("/ws") ||
            requestURI.startsWith("/test") ||
            requestURI.startsWith("/simple-test")) {
            System.out.println("=== JWT FILTER: Skipping authentication for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtUtil.extractUsername(jwt);
            
            System.out.println("=== JWT FILTER: Extracted email: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(userEmail).orElse(null);
                
                System.out.println("=== JWT FILTER: User lookup result: " + (user != null ? "FOUND" : "NOT FOUND"));
                if (user != null) {
                    System.out.println("=== JWT FILTER: User ID: " + user.getId());
                    System.out.println("=== JWT FILTER: User email: " + user.getEmail());
                    System.out.println("=== JWT FILTER: User role: " + user.getRole());
                    System.out.println("=== JWT FILTER: User status: " + user.getStatus());
                }
                
                boolean tokenValid = jwtUtil.validateToken(jwt);
                System.out.println("=== JWT FILTER: Token validation result: " + tokenValid);
                
                if (user != null && tokenValid) {
                    String role = user.getRole().name();
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }
                    System.out.println("=== JWT FILTER: Final role: " + role);
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("=== JWT FILTER: Authentication set successfully");
                } else {
                    System.out.println("=== JWT FILTER: Authentication failed");
                    if (user == null) {
                        System.out.println("=== JWT FILTER: Reason: User not found in database");
                    }
                    if (!tokenValid) {
                        System.out.println("=== JWT FILTER: Reason: Token validation failed");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("=== JWT FILTER: Exception during authentication: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
} 