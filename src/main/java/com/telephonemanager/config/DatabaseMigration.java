package com.telephonemanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Check if columns exist before adding them
        try {
            // Add serial_number column
            jdbcTemplate.execute("ALTER TABLE phone ADD COLUMN IF NOT EXISTS serial_number VARCHAR(255)");
            
            // Add condition column
            jdbcTemplate.execute("ALTER TABLE phone ADD COLUMN IF NOT EXISTS condition VARCHAR(50) DEFAULT 'EXCELLENT'");
            
            // Add storage column
            jdbcTemplate.execute("ALTER TABLE phone ADD COLUMN IF NOT EXISTS storage VARCHAR(100)");
            
            // Add color column
            jdbcTemplate.execute("ALTER TABLE phone ADD COLUMN IF NOT EXISTS color VARCHAR(100)");
            
            // Add price column
            jdbcTemplate.execute("ALTER TABLE phone ADD COLUMN IF NOT EXISTS price DOUBLE PRECISION");
            
            System.out.println("Database migration completed successfully!");
        } catch (Exception e) {
            System.err.println("Database migration failed: " + e.getMessage());
        }
    }
} 