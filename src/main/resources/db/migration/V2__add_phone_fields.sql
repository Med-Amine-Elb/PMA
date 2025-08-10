-- Add new fields to phone table
ALTER TABLE phone ADD serial_number VARCHAR(255);
ALTER TABLE phone ADD condition VARCHAR(50) NOT NULL DEFAULT 'EXCELLENT';
ALTER TABLE phone ADD storage VARCHAR(100);
ALTER TABLE phone ADD color VARCHAR(100);
ALTER TABLE phone ADD price DOUBLE PRECISION; 