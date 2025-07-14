package com.telephonemanager.controller;

import com.telephonemanager.dto.MessageDto;
import com.telephonemanager.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    // Get messages by conversation with pagination
    @GetMapping("/conversation/{conversationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> getMessagesByConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MessageDto> messages = messageService.getMessagesByConversation(conversationId, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Get message by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long id) {
        Optional<MessageDto> message = messageService.getMessageById(id);
        return message.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new message
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        MessageDto createdMessage = messageService.createMessage(messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
    
    // Update message
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long id, 
            @RequestBody MessageDto messageDto) {
        Optional<MessageDto> updatedMessage = messageService.updateMessage(id, messageDto);
        return updatedMessage.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete message
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        boolean deleted = messageService.deleteMessage(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Get messages by sender
    @GetMapping("/sender/{senderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> getMessagesBySender(
            @PathVariable Long senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.getMessagesBySender(senderId, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Get unread messages for user in conversation
    @GetMapping("/unread/{conversationId}/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<MessageDto>> getUnreadMessagesForUser(
            @PathVariable Long conversationId, 
            @PathVariable Long userId) {
        
        List<MessageDto> messages = messageService.getUnreadMessagesForUser(conversationId, userId);
        return ResponseEntity.ok(messages);
    }
    
    // Get starred messages by user
    @GetMapping("/starred/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> getStarredMessagesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.getStarredMessagesByUser(userId, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Get messages by type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> getMessagesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.getMessagesByType(type, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Search messages by content
    @GetMapping("/search/content")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> searchMessagesByContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.searchMessagesByContent(content, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Get messages after a specific time
    @GetMapping("/after-time")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<MessageDto>> getMessagesAfterTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sentAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = messageService.getMessagesAfterTime(sentAt, pageable);
        return ResponseEntity.ok(messages);
    }
    
    // Get messages after a specific message
    @GetMapping("/after-message/{conversationId}/{messageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<MessageDto>> getMessagesAfterMessage(
            @PathVariable Long conversationId, 
            @PathVariable Long messageId) {
        
        List<MessageDto> messages = messageService.getMessagesAfterMessage(conversationId, messageId);
        return ResponseEntity.ok(messages);
    }
    
    // Get unread message count for user in conversation
    @GetMapping("/unread-count/{conversationId}/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Long conversationId, 
            @PathVariable Long userId) {
        
        Long count = messageService.getUnreadMessageCount(conversationId, userId);
        return ResponseEntity.ok(count);
    }
    
    // Mark message as read
    @PutMapping("/{messageId}/read/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> markMessageAsRead(
            @PathVariable Long messageId, 
            @PathVariable Long userId) {
        
        boolean success = messageService.markMessageAsRead(messageId, userId);
        return ResponseEntity.ok(success);
    }
    
    // Toggle message star
    @PutMapping("/{messageId}/star/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> toggleMessageStar(
            @PathVariable Long messageId, 
            @PathVariable Long userId) {
        
        boolean success = messageService.toggleMessageStar(messageId, userId);
        return ResponseEntity.ok(success);
    }
    
    // Mark all messages as read in conversation
    @PutMapping("/mark-all-read/{conversationId}/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> markAllMessagesAsRead(
            @PathVariable Long conversationId, 
            @PathVariable Long userId) {
        
        boolean success = messageService.markAllMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok(success);
    }
} 