package com.telephonemanager.config;

import com.telephonemanager.entity.User;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if they don't exist
        if (userRepository.count() == 0) {
            createTestUsers();
        }
    }

    private void createTestUsers() {
        // Admin user
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@company.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.UserRole.ADMIN);
        admin.setDepartment("IT");
        admin.setPosition("System Administrator");
        admin.setStatus(User.UserStatus.ACTIVE);
        admin.setJoinDate(LocalDate.now());
        admin.setPhone("+33 6 12 34 56 78");
        admin.setAddress("123 Admin St, Paris");
        admin.setManager("CEO");
        userRepository.save(admin);

        // Assigner user
        User assigner = new User();
        assigner.setName("Assigner User");
        assigner.setEmail("assigner@company.com");
        assigner.setPassword(passwordEncoder.encode("assigner123"));
        assigner.setRole(User.UserRole.ASSIGNER);
        assigner.setDepartment("HR");
        assigner.setPosition("HR Manager");
        assigner.setStatus(User.UserStatus.ACTIVE);
        assigner.setJoinDate(LocalDate.now());
        assigner.setPhone("+33 6 12 34 56 79");
        assigner.setAddress("456 HR St, Paris");
        assigner.setManager("HR Director");
        userRepository.save(assigner);

        // Regular user
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@company.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(User.UserRole.USER);
        user.setDepartment("Sales");
        user.setPosition("Sales Representative");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setJoinDate(LocalDate.now());
        user.setPhone("+33 6 12 34 56 80");
        user.setAddress("789 Sales St, Paris");
        user.setManager("Sales Manager");
        userRepository.save(user);

        System.out.println("Test users created successfully!");
        System.out.println("Admin: admin@company.com / admin123");
        System.out.println("Assigner: assigner@company.com / assigner123");
        System.out.println("User: john@company.com / user123");
    }
} 