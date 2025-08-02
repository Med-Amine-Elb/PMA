package com.telephonemanager.controller;

import com.telephonemanager.dto.MessageDto;
import com.telephonemanager.service.MessageService;
import com.telephonemanager.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class WebSocketMessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message")
    public void handleMessage(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        // Add timestamp if not present
        if (messageDto.getSentAt() == null) {
            messageDto.setSentAt(LocalDateTime.now());
        }
        
        // Set sender name if not present
        if (messageDto.getSenderName() == null && headerAccessor.getSessionAttributes().get("username") != null) {
            messageDto.setSenderName((String) headerAccessor.getSessionAttributes().get("username"));
        }
        
        // Save message to database
        MessageDto savedMessage = messageService.createMessage(messageDto);
        
        // Broadcast to conversation-specific topic
        if (savedMessage.getConversationId() != null) {
            messagingTemplate.convertAndSend("/topic/conversation/" + savedMessage.getConversationId(), savedMessage);
        }
        
        // Also broadcast to general topic for admin/assigner dashboards
        messagingTemplate.convertAndSend("/topic/messages", savedMessage);
    }

    @MessageMapping("/join")
    @SendTo("/topic/messages")
    public MessageDto handleUserJoin(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to WebSocket session
        headerAccessor.getSessionAttributes().put("username", messageDto.getSenderName());
        
        // Create join notification message
        MessageDto joinMessage = new MessageDto();
        joinMessage.setContent(messageDto.getSenderName() + " joined the chat");
        joinMessage.setSenderName("System");
        joinMessage.setSentAt(LocalDateTime.now());
        joinMessage.setType("JOIN");
        
        return joinMessage;
    }

    @MessageMapping("/join-conversation")
    public void handleJoinConversation(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to conversation-specific session
        if (messageDto.getConversationId() != null) {
            String conversationKey = "conversation_" + messageDto.getConversationId();
            headerAccessor.getSessionAttributes().put(conversationKey, true);
            
            // Send confirmation to user
            MessageDto confirmation = new MessageDto();
            confirmation.setContent("You joined conversation " + messageDto.getConversationId());
            confirmation.setSenderName("System");
            confirmation.setSentAt(LocalDateTime.now());
            confirmation.setType("JOIN_CONVERSATION");
            confirmation.setConversationId(messageDto.getConversationId());
            
            messagingTemplate.convertAndSendToUser(
                headerAccessor.getSessionId(),
                "/queue/messages",
                confirmation
            );
        }
    }

    @MessageMapping("/leave")
    @SendTo("/topic/messages")
    public MessageDto handleUserLeave(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        // Create leave notification message
        MessageDto leaveMessage = new MessageDto();
        leaveMessage.setContent(messageDto.getSenderName() + " left the chat");
        leaveMessage.setSenderName("System");
        leaveMessage.setSentAt(LocalDateTime.now());
        leaveMessage.setType("LEAVE");
        
        return leaveMessage;
    }
} 