package com.telephonemanager.controller;

import com.telephonemanager.dto.MessageDto;
import com.telephonemanager.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void handleMessage(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Save message to database
            MessageDto savedMessage = chatService.sendMessage(messageDto);
            
            // Broadcast to conversation participants
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + savedMessage.getConversationId(), 
                savedMessage
            );
            
            // Send to specific user if it's a direct message
            if (savedMessage.getConversationId() != null) {
                messagingTemplate.convertAndSendToUser(
                    savedMessage.getSenderId().toString(),
                    "/queue/messages",
                    savedMessage
                );
            }
            
        } catch (Exception e) {
            // Send error message back to sender
            MessageDto errorMessage = new MessageDto();
            errorMessage.setContent("Error sending message: " + e.getMessage());
            errorMessage.setType("SYSTEM");
            errorMessage.setConversationId(messageDto.getConversationId());
            
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId().toString(),
                "/queue/errors",
                errorMessage
            );
        }
    }

    @MessageMapping("/chat/join")
    public void handleJoinConversation(@Payload JoinRequestDto request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Add user to conversation if needed
            // This could be used for group conversations
            
            // Send join notification
            MessageDto joinMessage = new MessageDto();
            joinMessage.setContent("User joined the conversation");
            joinMessage.setType("SYSTEM");
            joinMessage.setConversationId(request.getConversationId());
            joinMessage.setSenderId(request.getUserId());
            
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(), 
                joinMessage
            );
            
        } catch (Exception e) {
            // Handle error
            System.err.println("Error joining conversation: " + e.getMessage());
        }
    }

    @MessageMapping("/chat/typing")
    public void handleTyping(@Payload TypingRequestDto request, SimpMessageHeaderAccessor headerAccessor) {
        // Broadcast typing indicator to conversation participants
        TypingNotificationDto typingNotification = new TypingNotificationDto();
        typingNotification.setUserId(request.getUserId());
        typingNotification.setUserName(request.getUserName());
        typingNotification.setConversationId(request.getConversationId());
        typingNotification.setTyping(request.isTyping());
        
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + request.getConversationId() + "/typing",
            typingNotification
        );
    }

    @MessageMapping("/chat/read")
    public void handleMessageRead(@Payload ReadRequestDto request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Mark message as read
            chatService.markMessageAsRead(request.getMessageId(), request.getUserId());
            
            // Broadcast read receipt to conversation participants
            ReadReceiptDto readReceipt = new ReadReceiptDto();
            readReceipt.setMessageId(request.getMessageId());
            readReceipt.setUserId(request.getUserId());
            readReceipt.setUserName(request.getUserName());
            readReceipt.setConversationId(request.getConversationId());
            readReceipt.setReadAt(java.time.LocalDateTime.now());
            
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId() + "/read",
                readReceipt
            );
            
        } catch (Exception e) {
            System.err.println("Error marking message as read: " + e.getMessage());
        }
    }

    // DTO classes for WebSocket requests
    public static class JoinRequestDto {
        private Long conversationId;
        private Long userId;
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static class TypingRequestDto {
        private Long conversationId;
        private Long userId;
        private String userName;
        private boolean typing;
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class TypingNotificationDto {
        private Long userId;
        private String userName;
        private Long conversationId;
        private boolean typing;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class ReadRequestDto {
        private Long messageId;
        private Long userId;
        private String userName;
        private Long conversationId;
        
        public Long getMessageId() { return messageId; }
        public void setMessageId(Long messageId) { this.messageId = messageId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    }

    public static class ReadReceiptDto {
        private Long messageId;
        private Long userId;
        private String userName;
        private Long conversationId;
        private java.time.LocalDateTime readAt;
        
        public Long getMessageId() { return messageId; }
        public void setMessageId(Long messageId) { this.messageId = messageId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        
        public java.time.LocalDateTime getReadAt() { return readAt; }
        public void setReadAt(java.time.LocalDateTime readAt) { this.readAt = readAt; }
    }
} 