package com.telephonemanager.service;

import com.telephonemanager.dto.LoginRequest;
import com.telephonemanager.dto.LoginResponse;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Votre compte a été désactivé. Veuillez contacter l'administrateur pour plus d'informations.");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Create response
        LoginResponse.LoginData loginData = new LoginResponse.LoginData();
        loginData.setToken(token);
        loginData.setUser(new LoginResponse.UserResponse(user));

        return new LoginResponse(true, loginData);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void logout(String email) {
        // In a real application, you might want to blacklist the token
        // For now, we'll just log the logout
        System.out.println("User " + email + " logged out");
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }
} 