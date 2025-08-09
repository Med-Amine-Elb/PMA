-- Database Migration Script for Phone Management
-- Run this script in SQL Server Management Studio

USE PhoneManagementDB;

-- Check if columns already exist before adding them
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'phone' AND COLUMN_NAME = 'serial_number')
BEGIN
    ALTER TABLE phone ADD serial_number VARCHAR(255);
    PRINT 'Added serial_number column';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'phone' AND COLUMN_NAME = 'condition')
BEGIN
    ALTER TABLE phone ADD condition VARCHAR(50) NOT NULL DEFAULT 'EXCELLENT';
    PRINT 'Added condition column';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'phone' AND COLUMN_NAME = 'storage')
BEGIN
    ALTER TABLE phone ADD storage VARCHAR(100);
    PRINT 'Added storage column';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'phone' AND COLUMN_NAME = 'color')
BEGIN
    ALTER TABLE phone ADD color VARCHAR(100);
    PRINT 'Added color column';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'phone' AND COLUMN_NAME = 'price')
BEGIN
    ALTER TABLE phone ADD price DOUBLE PRECISION;
    PRINT 'Added price column';
END

-- Verify the migration
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'phone' 
ORDER BY ORDINAL_POSITION;

PRINT 'Database migration completed successfully!'; 