package com.telephonemanager.config;

import com.telephonemanager.entity.User;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.repository.PhoneRepository;
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
    private PhoneRepository phoneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== DataInitializer starting ===");
        
        try {
            // Check if database is accessible
            long userCount = userRepository.count();
            System.out.println("Current user count: " + userCount);
            
            // Create test users if they don't exist
            if (userCount == 0) {
                System.out.println("No users found, creating test users...");
                createTestUsers();
                System.out.println("Test users created successfully!");
            } else {
                System.out.println("Users already exist, skipping creation.");
            }
            
            // Create test phones if they don't exist
            long phoneCount = phoneRepository.count();
            System.out.println("Current phone count: " + phoneCount);
            
            if (phoneCount == 0) {
                System.out.println("No phones found, creating test phones...");
                createTestPhones();
                System.out.println("Test phones created successfully!");
            } else {
                System.out.println("Phones already exist, skipping creation.");
            }
            
            // Verify data exists
            userRepository.findAll().forEach(user -> {
                System.out.println("User: " + user.getEmail() + " (Role: " + user.getRole() + ")");
            });
            
            phoneRepository.findAll().forEach(phone -> {
                System.out.println("Phone: " + phone.getBrand() + " " + phone.getModel() + " (" + phone.getColor() + ")");
            });
            
        } catch (Exception e) {
            System.err.println("Error in DataInitializer: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== DataInitializer completed ===");
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
        System.out.println("Admin user created: admin@company.com");

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
        System.out.println("Assigner user created: assigner@company.com");

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
        System.out.println("User created: john@company.com");

        System.out.println("Test users created successfully!");
        System.out.println("Admin: admin@company.com / admin123");
        System.out.println("Assigner: assigner@company.com / assigner123");
        System.out.println("User: john@company.com / user123");
    }

    private void createTestPhones() {
        // iPhone 15 Pro - Space Black
        Phone iphone1 = new Phone();
        iphone1.setBrand("Apple");
        iphone1.setModel("iPhone 15 Pro");
        iphone1.setImei("123456789012345");
        iphone1.setSerialNumber("APL123456789");
        iphone1.setStatus(Phone.Status.AVAILABLE);
        iphone1.setCondition(Phone.Condition.EXCELLENT);
        iphone1.setStorage("256GB");
        iphone1.setColor("Space Black");
        iphone1.setPrice(1199.0);
        phoneRepository.save(iphone1);
        System.out.println("Phone created: iPhone 15 Pro Space Black");

        // iPhone 15 Pro - Gold
        Phone iphone2 = new Phone();
        iphone2.setBrand("Apple");
        iphone2.setModel("iPhone 15 Pro");
        iphone2.setImei("123456789012346");
        iphone2.setSerialNumber("APL123456790");
        iphone2.setStatus(Phone.Status.AVAILABLE);
        iphone2.setCondition(Phone.Condition.EXCELLENT);
        iphone2.setStorage("128GB");
        iphone2.setColor("Gold");
        iphone2.setPrice(1099.0);
        phoneRepository.save(iphone2);
        System.out.println("Phone created: iPhone 15 Pro Gold");

        // Samsung Galaxy S21 - Phantom Black
        Phone samsung1 = new Phone();
        samsung1.setBrand("Samsung");
        samsung1.setModel("Galaxy S21");
        samsung1.setImei("123456789012347");
        samsung1.setSerialNumber("SMS123456789");
        samsung1.setStatus(Phone.Status.AVAILABLE);
        samsung1.setCondition(Phone.Condition.GOOD);
        samsung1.setStorage("128GB");
        samsung1.setColor("Phantom Black");
        samsung1.setPrice(899.0);
        phoneRepository.save(samsung1);
        System.out.println("Phone created: Samsung Galaxy S21 Phantom Black");

        // Samsung Galaxy S22 - Green
        Phone samsung2 = new Phone();
        samsung2.setBrand("Samsung");
        samsung2.setModel("Galaxy S22");
        samsung2.setImei("123456789012348");
        samsung2.setSerialNumber("SMS123456790");
        samsung2.setStatus(Phone.Status.AVAILABLE);
        samsung2.setCondition(Phone.Condition.EXCELLENT);
        samsung2.setStorage("256GB");
        samsung2.setColor("Green");
        samsung2.setPrice(999.0);
        phoneRepository.save(samsung2);
        System.out.println("Phone created: Samsung Galaxy S22 Green");

        // Samsung Galaxy S22 - Burgundy
        Phone samsung3 = new Phone();
        samsung3.setBrand("Samsung");
        samsung3.setModel("Galaxy S22");
        samsung3.setImei("123456789012349");
        samsung3.setSerialNumber("SMS123456791");
        samsung3.setStatus(Phone.Status.AVAILABLE);
        samsung3.setCondition(Phone.Condition.EXCELLENT);
        samsung3.setStorage("128GB");
        samsung3.setColor("Burgundy");
        samsung3.setPrice(999.0);
        phoneRepository.save(samsung3);
        System.out.println("Phone created: Samsung Galaxy S22 Burgundy");

        System.out.println("Test phones created successfully!");
        System.out.println("Created 5 phones with different colors and specifications");
    }
} 