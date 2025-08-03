package com.telephonemanager.service;

import com.telephonemanager.dto.ConversationDto;
import com.telephonemanager.dto.MessageDto;
import com.telephonemanager.entity.Conversation;
import com.telephonemanager.entity.Message;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.ConversationRepository;
import com.telephonemanager.repository.MessageRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Conversation methods
    public ConversationDto createConversation(ConversationDto conversationDto) {
        User createdBy = userRepository.findById(conversationDto.getCreatedById())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Conversation conversation = new Conversation();
        conversation.setTitle(conversationDto.getTitle());
        conversation.setDescription(conversationDto.getDescription());
        conversation.setType(Conversation.ConversationType.valueOf(conversationDto.getType()));
        conversation.setCreatedBy(createdBy);
        
        // Add participants
        if (conversationDto.getParticipantIds() != null) {
            Set<User> participants = conversationDto.getParticipantIds().stream()
                    .map(userId -> userRepository.findById(userId).orElse(null))
                    .filter(user -> user != null)
                    .collect(Collectors.toSet());
            participants.add(createdBy); // Add creator as participant
            conversation.setParticipants(participants);
        }
        
        Conversation savedConversation = conversationRepository.save(conversation);
        return convertToConversationDto(savedConversation);
    }
    
    public List<ConversationDto> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsUserId(userId);
        return conversations.stream()
                .map(this::convertToConversationDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ConversationDto> getConversationById(Long conversationId, Long userId) {
        Optional<Conversation> conversation = conversationRepository.findByIdAndParticipantsUserId(conversationId, userId);
        return conversation.map(this::convertToConversationDto);
    }
    
    public Optional<ConversationDto> getOrCreateDirectConversation(Long userId1, Long userId2) {
        Optional<Conversation> existingConversation = conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
        
        if (existingConversation.isPresent()) {
            return existingConversation.map(this::convertToConversationDto);
        }
        
        // Create new direct conversation
        User user1 = userRepository.findById(userId1).orElseThrow(() -> new RuntimeException("User 1 not found"));
        User user2 = userRepository.findById(userId2).orElseThrow(() -> new RuntimeException("User 2 not found"));
        
        ConversationDto conversationDto = new ConversationDto();
        conversationDto.setTitle(user1.getName() + " & " + user2.getName());
        conversationDto.setType("DIRECT");
        conversationDto.setCreatedById(userId1);
        conversationDto.setParticipantIds(Set.of(userId1, userId2));
        
        ConversationDto createdConversation = createConversation(conversationDto);
        return Optional.of(createdConversation);
    }
    
    // Message methods
    public MessageDto sendMessage(MessageDto messageDto) {
        Conversation conversation = conversationRepository.findById(messageDto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(messageDto.getContent());
        message.setType(Message.MessageType.valueOf(messageDto.getType()));
        
        Message savedMessage = messageRepository.save(message);
        
        // Update conversation's last message time
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return convertToMessageDto(savedMessage);
    }
    
    public List<MessageDto> getConversationMessages(Long conversationId, Long userId) {
        // Verify user has access to conversation
        conversationRepository.findByIdAndParticipantsUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Access denied"));
        
        List<Message> messages = messageRepository.findByConversationOrderBySentAtAsc(conversationId);
        return messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getNewMessages(Long conversationId, Long lastMessageId, Long userId) {
        // Verify user has access to conversation
        conversationRepository.findByIdAndParticipantsUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Access denied"));
        
        List<Message> messages = messageRepository.findByConversationAndIdGreaterThan(conversationId, lastMessageId);
        return messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }
    
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<User> readBy = message.getReadBy();
        readBy.add(user);
        message.setReadBy(readBy);
        
        messageRepository.save(message);
    }
    
    public void markAllMessagesAsRead(Long conversationId, Long userId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessagesForUser(conversationId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        for (Message message : unreadMessages) {
            Set<User> readBy = message.getReadBy();
            readBy.add(user);
            message.setReadBy(readBy);
            messageRepository.save(message);
        }
    }
    
    public long getUnreadMessageCount(Long conversationId, Long userId) {
        return messageRepository.countUnreadMessagesForUser(conversationId, userId);
    }
    
    // Conversion methods
    private ConversationDto convertToConversationDto(Conversation conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setDescription(conversation.getDescription());
        dto.setType(conversation.getType().name());
        dto.setCreatedById(conversation.getCreatedBy().getId());
        dto.setCreatedByName(conversation.getCreatedBy().getName());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        dto.setActive(conversation.isActive());
        
        if (conversation.getParticipants() != null) {
            dto.setParticipantIds(conversation.getParticipants().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet()));
            dto.setParticipantNames(conversation.getParticipants().stream()
                    .map(User::getName)
                    .collect(Collectors.toList()));
        }
        
        // Get last message info
        Message lastMessage = messageRepository.findLastMessageByConversation(conversation.getId());
        if (lastMessage != null) {
            dto.setLastMessageContent(lastMessage.getContent());
            dto.setLastMessageSenderName(lastMessage.getSender().getName());
        }
        
        return dto;
    }
    
    private MessageDto convertToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setContent(message.getContent());
        dto.setType(message.getType().name());
        dto.setSentAt(message.getSentAt());
        dto.setEditedAt(message.getEditedAt());
        dto.setEdited(message.isEdited());
        dto.setStarred(message.isStarred());
        dto.setReplyToId(message.getReplyToId());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        
        if (message.getReadBy() != null) {
            dto.setReadByUserIds(message.getReadBy().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet()));
            dto.setReadByUserNames(message.getReadBy().stream()
                    .map(User::getName)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
} 