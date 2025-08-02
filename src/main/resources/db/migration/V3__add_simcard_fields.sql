USE PhoneManagementDB;

-- Add carrier column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sim_card' AND COLUMN_NAME = 'carrier')
BEGIN
    ALTER TABLE sim_card ADD carrier VARCHAR(255);
    PRINT 'Added carrier column';
END

-- Add plan column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sim_card' AND COLUMN_NAME = 'plan')
BEGIN
    ALTER TABLE sim_card ADD [plan] VARCHAR(255);
    PRINT 'Added plan column';
END

-- Add monthly_fee column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sim_card' AND COLUMN_NAME = 'monthly_fee')
BEGIN
    ALTER TABLE sim_card ADD monthly_fee DECIMAL(10,2);
    PRINT 'Added monthly_fee column';
END

-- Add data_limit column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sim_card' AND COLUMN_NAME = 'data_limit')
BEGIN
    ALTER TABLE sim_card ADD data_limit VARCHAR(255);
    PRINT 'Added data_limit column';
END

-- Add expiry_date column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sim_card' AND COLUMN_NAME = 'expiry_date')
BEGIN
    ALTER TABLE sim_card ADD expiry_date DATE;
    PRINT 'Added expiry_date column';
END

PRINT 'SIM card migration completed successfully!'; 