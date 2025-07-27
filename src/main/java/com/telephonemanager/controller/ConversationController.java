package com.telephonemanager.controller;

import com.telephonemanager.dto.ConversationDto;
import com.telephonemanager.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    // Get all conversations with pagination
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> getAllConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ConversationDto> conversations = conversationService.getAllConversations(pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get conversation by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<ConversationDto> getConversationById(@PathVariable Long id) {
        Optional<ConversationDto> conversation = conversationService.getConversationById(id);
        return conversation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new conversation
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<ConversationDto> createConversation(@RequestBody ConversationDto conversationDto) {
        ConversationDto createdConversation = conversationService.createConversation(conversationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConversation);
    }
    
    // Update conversation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<ConversationDto> updateConversation(
            @PathVariable Long id, 
            @RequestBody ConversationDto conversationDto) {
        Optional<ConversationDto> updatedConversation = conversationService.updateConversation(id, conversationDto);
        return updatedConversation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete conversation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        boolean deleted = conversationService.deleteConversation(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Get conversations by participant
    @GetMapping("/participant/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> getConversationsByParticipant(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByParticipant(userId, pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get conversations by type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> getConversationsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByType(type, pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get conversations by creator
    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> getConversationsByCreator(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsByCreator(creatorId, pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get direct conversation between two users
    @GetMapping("/direct/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<ConversationDto>> getDirectConversationBetweenUsers(
            @PathVariable Long userId1, 
            @PathVariable Long userId2) {
        
        List<ConversationDto> conversations = conversationService.getDirectConversationBetweenUsers(userId1, userId2);
        return ResponseEntity.ok(conversations);
    }
    
    // Get or create direct conversation between two users
    @PostMapping("/direct/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<ConversationDto> getOrCreateDirectConversation(
            @PathVariable Long userId1, 
            @PathVariable Long userId2) {
        
        ConversationDto conversation = conversationService.getOrCreateDirectConversation(userId1, userId2);
        return conversation != null ? ResponseEntity.ok(conversation) : ResponseEntity.badRequest().build();
    }
    
    // Search conversations by title
    @GetMapping("/search/title")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> searchConversationsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.searchConversationsByTitle(title, pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get conversations with recent activity
    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<ConversationDto>> getConversationsWithRecentActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = conversationService.getConversationsWithRecentActivity(pageable);
        return ResponseEntity.ok(conversations);
    }
    
    // Get conversations with unread messages for user
    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<ConversationDto>> getConversationsWithUnreadMessages(@PathVariable Long userId) {
        List<ConversationDto> conversations = conversationService.getConversationsWithUnreadMessages(userId);
        return ResponseEntity.ok(conversations);
    }
} 