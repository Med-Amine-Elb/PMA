package com.telephonemanager.service;

import com.telephonemanager.dto.ConversationDto;
import com.telephonemanager.entity.Conversation;
import com.telephonemanager.entity.Message;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.ConversationRepository;
import com.telephonemanager.repository.MessageRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<ConversationDto> getAllConversations(Pageable pageable) {
        return conversationRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDto);
    }
    
    public Optional<ConversationDto> getConversationById(Long id) {
        return conversationRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public ConversationDto createConversation(ConversationDto conversationDto) {
        Conversation conversation = convertToEntity(conversationDto);
        Conversation savedConversation = conversationRepository.save(conversation);
        return convertToDto(savedConversation);
    }
    
    public Optional<ConversationDto> updateConversation(Long id, ConversationDto conversationDto) {
        return conversationRepository.findById(id)
                .map(existingConversation -> {
                    updateConversationFromDto(existingConversation, conversationDto);
                    Conversation savedConversation = conversationRepository.save(existingConversation);
                    return convertToDto(savedConversation);
                });
    }
    
    public boolean deleteConversation(Long id) {
        if (conversationRepository.existsById(id)) {
            Conversation conversation = conversationRepository.findById(id).get();
            conversation.setActive(false);
            conversationRepository.save(conversation);
            return true;
        }
        return false;
    }
    
    public Page<ConversationDto> getConversationsByParticipant(Long userId, Pageable pageable) {
        return conversationRepository.findByParticipantId(userId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<ConversationDto> getConversationsByType(String type, Pageable pageable) {
        try {
            Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(type.toUpperCase());
            return conversationRepository.findByType(conversationType, pageable)
                    .map(this::convertToDto);
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }
    
    public Page<ConversationDto> getConversationsByCreator(Long creatorId, Pageable pageable) {
        Optional<User> creator = userRepository.findById(creatorId);
        if (creator.isPresent()) {
            return conversationRepository.findByCreatedBy(creator.get(), pageable)
                    .map(this::convertToDto);
        }
        return Page.empty(pageable);
    }
    
    public List<ConversationDto> getDirectConversationBetweenUsers(Long userId1, Long userId2) {
        return conversationRepository.findDirectConversationBetweenUsers(userId1, userId2)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<ConversationDto> searchConversationsByTitle(String title, Pageable pageable) {
        return conversationRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::convertToDto);
    }
    
    public Page<ConversationDto> getConversationsWithRecentActivity(Pageable pageable) {
        return conversationRepository.findConversationsWithRecentActivity(pageable)
                .map(this::convertToDto);
    }
    
    public List<ConversationDto> getConversationsWithUnreadMessages(Long userId) {
        return conversationRepository.findConversationsWithUnreadMessages(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ConversationDto getOrCreateDirectConversation(Long userId1, Long userId2) {
        List<Conversation> existingConversations = conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
        
        if (!existingConversations.isEmpty()) {
            return convertToDto(existingConversations.get(0));
        }
        
        // Create new direct conversation
        Optional<User> user1 = userRepository.findById(userId1);
        Optional<User> user2 = userRepository.findById(userId2);
        
        if (user1.isPresent() && user2.isPresent()) {
            Conversation newConversation = new Conversation();
            newConversation.setTitle("Direct Message");
            newConversation.setType(Conversation.ConversationType.DIRECT);
            newConversation.setCreatedBy(user1.get());
            newConversation.setParticipants(Set.of(user1.get(), user2.get()));
            newConversation.setActive(true);
            
            Conversation savedConversation = conversationRepository.save(newConversation);
            return convertToDto(savedConversation);
        }
        
        return null;
    }
    
    private ConversationDto convertToDto(Conversation conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setDescription(conversation.getDescription());
        dto.setType(conversation.getType().name());
        dto.setCreatedById(conversation.getCreatedBy().getId());
        dto.setCreatedByName(conversation.getCreatedBy().getName());
        dto.setParticipantIds(conversation.getParticipants().stream().map(User::getId).collect(Collectors.toSet()));
        dto.setParticipantNames(conversation.getParticipants().stream().map(User::getName).collect(Collectors.toSet()));
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        dto.setActive(conversation.isActive());
        
        // Get last message info
        List<Message> lastMessages = messageRepository.findLastMessageInConversation(conversation.getId(), Pageable.ofSize(1));
        if (!lastMessages.isEmpty()) {
            Message lastMessage = lastMessages.get(0);
            dto.setLastMessageContent(lastMessage.getContent());
            dto.setLastMessageSenderName(lastMessage.getSender().getName());
        }
        
        return dto;
    }
    
    private Conversation convertToEntity(ConversationDto dto) {
        Conversation conversation = new Conversation();
        conversation.setTitle(dto.getTitle());
        conversation.setDescription(dto.getDescription());
        conversation.setType(Conversation.ConversationType.valueOf(dto.getType()));
        conversation.setActive(dto.isActive());
        
        // Set creator
        if (dto.getCreatedById() != null) {
            userRepository.findById(dto.getCreatedById()).ifPresent(conversation::setCreatedBy);
        }
        
        // Set participants
        if (dto.getParticipantIds() != null) {
            Set<User> participants = dto.getParticipantIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            conversation.setParticipants(participants);
        }
        
        return conversation;
    }
    
    private void updateConversationFromDto(Conversation conversation, ConversationDto dto) {
        if (dto.getTitle() != null) conversation.setTitle(dto.getTitle());
        if (dto.getDescription() != null) conversation.setDescription(dto.getDescription());
        if (dto.getType() != null) conversation.setType(Conversation.ConversationType.valueOf(dto.getType()));
        conversation.setActive(dto.isActive());
        
        // Update participants if provided
        if (dto.getParticipantIds() != null) {
            Set<User> participants = dto.getParticipantIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            conversation.setParticipants(participants);
        }
    }
} 