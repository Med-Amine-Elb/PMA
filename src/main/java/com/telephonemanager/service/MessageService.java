package com.telephonemanager.service;

import com.telephonemanager.dto.MessageDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<MessageDto> getMessagesByConversation(Long conversationId, Pageable pageable) {
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);
        if (conversation.isPresent()) {
            return messageRepository.findByConversationOrderBySentAtDesc(conversation.get(), pageable)
                    .map(this::convertToDto);
        }
        return Page.empty(pageable);
    }
    
    public Optional<MessageDto> getMessageById(Long id) {
        return messageRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public MessageDto createMessage(MessageDto messageDto) {
        Message message = convertToEntity(messageDto);
        Message savedMessage = messageRepository.save(message);
        
        // Update conversation's last message time
        if (message.getConversation() != null) {
            Conversation conversation = message.getConversation();
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
        
        return convertToDto(savedMessage);
    }
    
    public Optional<MessageDto> updateMessage(Long id, MessageDto messageDto) {
        return messageRepository.findById(id)
                .map(existingMessage -> {
                    updateMessageFromDto(existingMessage, messageDto);
                    existingMessage.setEdited(true);
                    existingMessage.setEditedAt(LocalDateTime.now());
                    Message savedMessage = messageRepository.save(existingMessage);
                    return convertToDto(savedMessage);
                });
    }
    
    public boolean deleteMessage(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Page<MessageDto> getMessagesBySender(Long senderId, Pageable pageable) {
        Optional<User> sender = userRepository.findById(senderId);
        if (sender.isPresent()) {
            return messageRepository.findBySenderOrderBySentAtDesc(sender.get(), pageable)
                    .map(this::convertToDto);
        }
        return Page.empty(pageable);
    }
    
    public List<MessageDto> getUnreadMessagesForUser(Long conversationId, Long userId) {
        return messageRepository.findUnreadMessagesForUser(conversationId, userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<MessageDto> getStarredMessagesByUser(Long userId, Pageable pageable) {
        return messageRepository.findStarredMessagesByUser(userId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<MessageDto> getMessagesByType(String type, Pageable pageable) {
        try {
            Message.MessageType messageType = Message.MessageType.valueOf(type.toUpperCase());
            return messageRepository.findByType(messageType, pageable)
                    .map(this::convertToDto);
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }
    
    public Page<MessageDto> searchMessagesByContent(String content, Pageable pageable) {
        return messageRepository.findByContentContainingIgnoreCase(content, pageable)
                .map(this::convertToDto);
    }
    
    public Page<MessageDto> getMessagesAfterTime(LocalDateTime sentAt, Pageable pageable) {
        return messageRepository.findBySentAtAfterOrderBySentAtDesc(sentAt, pageable)
                .map(this::convertToDto);
    }
    
    public List<MessageDto> getMessagesAfterMessage(Long conversationId, Long messageId) {
        return messageRepository.findMessagesAfterMessage(conversationId, messageId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Long getUnreadMessageCount(Long conversationId, Long userId) {
        return messageRepository.countUnreadMessagesForUser(conversationId, userId);
    }
    
    public boolean markMessageAsRead(Long messageId, Long userId) {
        Optional<Message> message = messageRepository.findById(messageId);
        Optional<User> user = userRepository.findById(userId);
        
        if (message.isPresent() && user.isPresent()) {
            Message msg = message.get();
            Set<User> readBy = msg.getReadBy();
            readBy.add(user.get());
            msg.setReadBy(readBy);
            messageRepository.save(msg);
            return true;
        }
        return false;
    }
    
    public boolean toggleMessageStar(Long messageId, Long userId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            Message msg = message.get();
            msg.setStarred(!msg.isStarred());
            messageRepository.save(msg);
            return true;
        }
        return false;
    }
    
    public boolean markAllMessagesAsRead(Long conversationId, Long userId) {
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);
        Optional<User> user = userRepository.findById(userId);
        
        if (conversation.isPresent() && user.isPresent()) {
            List<Message> unreadMessages = messageRepository.findUnreadMessagesForUser(conversationId, userId);
            for (Message message : unreadMessages) {
                Set<User> readBy = message.getReadBy();
                readBy.add(user.get());
                message.setReadBy(readBy);
                messageRepository.save(message);
            }
            return true;
        }
        return false;
    }
    
    private MessageDto convertToDto(Message message) {
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
        dto.setReadByUserIds(message.getReadBy().stream().map(User::getId).collect(Collectors.toSet()));
        dto.setReadByUserNames(message.getReadBy().stream().map(User::getName).collect(Collectors.toSet()));
        dto.setReplyToId(message.getReplyToId());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        return dto;
    }
    
    private Message convertToEntity(MessageDto dto) {
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setType(Message.MessageType.valueOf(dto.getType()));
        message.setReplyToId(dto.getReplyToId());
        message.setAttachmentUrl(dto.getAttachmentUrl());
        message.setStarred(dto.isStarred());
        
        // Set conversation
        if (dto.getConversationId() != null) {
            conversationRepository.findById(dto.getConversationId()).ifPresent(message::setConversation);
        }
        
        // Set sender
        if (dto.getSenderId() != null) {
            userRepository.findById(dto.getSenderId()).ifPresent(message::setSender);
        }
        
        // Set read by users
        if (dto.getReadByUserIds() != null) {
            Set<User> readByUsers = dto.getReadByUserIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            message.setReadBy(readByUsers);
        }
        
        return message;
    }
    
    private void updateMessageFromDto(Message message, MessageDto dto) {
        if (dto.getContent() != null) message.setContent(dto.getContent());
        if (dto.getType() != null) message.setType(Message.MessageType.valueOf(dto.getType()));
        if (dto.getReplyToId() != null) message.setReplyToId(dto.getReplyToId());
        if (dto.getAttachmentUrl() != null) message.setAttachmentUrl(dto.getAttachmentUrl());
        message.setStarred(dto.isStarred());
        
        // Update read by users if provided
        if (dto.getReadByUserIds() != null) {
            Set<User> readByUsers = dto.getReadByUserIds().stream()
                    .map(userId -> userRepository.findById(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            message.setReadBy(readByUsers);
        }
    }
} 