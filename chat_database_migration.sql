-- Chat Database Migration Script
-- Run this script to create the chat-related tables

-- Conversations table
CREATE TABLE conversations (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    description NTEXT,
    type NVARCHAR(50) NOT NULL DEFAULT 'DIRECT',
    created_by BIGINT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    last_message_at DATETIME2,
    is_active BIT DEFAULT 1,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Conversation participants
CREATE TABLE conversation_participants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    conversation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at DATETIME2 DEFAULT GETDATE(),
    is_admin BIT DEFAULT 0,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(conversation_id, user_id)
);

-- Messages table
CREATE TABLE messages (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content NTEXT NOT NULL,
    message_type NVARCHAR(50) DEFAULT 'TEXT',
    sent_at DATETIME2 DEFAULT GETDATE(),
    edited_at DATETIME2,
    is_edited BIT DEFAULT 0,
    is_starred BIT DEFAULT 0,
    reply_to_id BIGINT,
    attachment_url NVARCHAR(500),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (reply_to_id) REFERENCES messages(id)
);

-- Message read status
CREATE TABLE message_read_by (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (message_id) REFERENCES messages(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(message_id, user_id)
);

-- Create indexes for better performance
CREATE INDEX IX_conversations_participants ON conversations(id);
CREATE INDEX IX_messages_conversation ON messages(conversation_id);
CREATE INDEX IX_messages_sender ON messages(sender_id);
CREATE INDEX IX_messages_sent_at ON messages(sent_at);
CREATE INDEX IX_message_read_by_message ON message_read_by(message_id);
CREATE INDEX IX_message_read_by_user ON message_read_by(user_id);

-- Insert some sample data for testing
-- Note: Make sure users with these IDs exist in your users table
-- You may need to adjust the user IDs based on your existing data

-- Sample conversations
INSERT INTO conversations (title, description, type, created_by, created_at, updated_at, is_active)
VALUES 
('General Support', 'General support and questions', 'GROUP', 1, GETDATE(), GETDATE(), 1),
('Technical Issues', 'Technical support and troubleshooting', 'GROUP', 1, GETDATE(), GETDATE(), 1),
('Admin Chat', 'Administrative discussions', 'GROUP', 1, GETDATE(), GETDATE(), 1);

-- Sample conversation participants
INSERT INTO conversation_participants (conversation_id, user_id, joined_at, is_admin)
VALUES 
(1, 1, GETDATE(), 1), -- Admin in General Support
(1, 2, GETDATE(), 0), -- User 2 in General Support
(1, 3, GETDATE(), 0), -- User 3 in General Support
(2, 1, GETDATE(), 1), -- Admin in Technical Issues
(2, 2, GETDATE(), 0), -- User 2 in Technical Issues
(3, 1, GETDATE(), 1); -- Admin in Admin Chat

-- Sample messages
INSERT INTO messages (conversation_id, sender_id, content, message_type, sent_at)
VALUES 
(1, 1, 'Welcome to the General Support chat! How can we help you today?', 'TEXT', GETDATE()),
(1, 2, 'Hi! I have a question about my phone assignment.', 'TEXT', DATEADD(MINUTE, 1, GETDATE())),
(1, 1, 'Sure! What would you like to know?', 'TEXT', DATEADD(MINUTE, 2, GETDATE())),
(2, 1, 'Technical support is now available. Please describe your issue.', 'TEXT', GETDATE()),
(3, 1, 'Admin meeting scheduled for tomorrow at 10 AM.', 'TEXT', GETDATE());

-- Sample read receipts
INSERT INTO message_read_by (message_id, user_id, read_at)
VALUES 
(1, 2, DATEADD(MINUTE, 1, GETDATE())),
(1, 3, DATEADD(MINUTE, 1, GETDATE())),
(2, 1, DATEADD(MINUTE, 2, GETDATE())),
(3, 1, DATEADD(MINUTE, 3, GETDATE())),
(4, 2, DATEADD(MINUTE, 1, GETDATE()));

PRINT 'Chat database migration completed successfully!';
PRINT 'Created tables: conversations, conversation_participants, messages, message_read_by';
PRINT 'Added sample data for testing'; 