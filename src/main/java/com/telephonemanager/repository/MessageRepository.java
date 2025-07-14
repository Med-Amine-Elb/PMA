package com.telephonemanager.repository;

import com.telephonemanager.entity.Conversation;
import com.telephonemanager.entity.Message;
import com.telephonemanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find messages by conversation
    Page<Message> findByConversationOrderBySentAtDesc(Conversation conversation, Pageable pageable);
    
    // Find messages by sender
    Page<Message> findBySenderOrderBySentAtDesc(User sender, Pageable pageable);
    
    // Find messages by conversation and sender
    Page<Message> findByConversationAndSenderOrderBySentAtDesc(Conversation conversation, User sender, Pageable pageable);
    
    // Find unread messages for a user in a conversation
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND :userId NOT IN " +
           "(SELECT r.id FROM m.readBy r) ORDER BY m.sentAt ASC")
    List<Message> findUnreadMessagesForUser(@Param("conversationId") Long conversationId, 
                                           @Param("userId") Long userId);
    
    // Find starred messages by user
    @Query("SELECT m FROM Message m JOIN m.conversation c JOIN c.participants p " +
           "WHERE p.id = :userId AND m.isStarred = true ORDER BY m.sentAt DESC")
    Page<Message> findStarredMessagesByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find messages by type
    Page<Message> findByType(Message.MessageType type, Pageable pageable);
    
    // Find messages by content containing
    Page<Message> findByContentContainingIgnoreCase(String content, Pageable pageable);
    
    // Find messages sent after a specific time
    Page<Message> findBySentAtAfterOrderBySentAtDesc(LocalDateTime sentAt, Pageable pageable);
    
    // Find messages in a conversation after a specific message
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.sentAt > " +
           "(SELECT m2.sentAt FROM Message m2 WHERE m2.id = :messageId) ORDER BY m.sentAt ASC")
    List<Message> findMessagesAfterMessage(@Param("conversationId") Long conversationId, 
                                          @Param("messageId") Long messageId);
    
    // Count unread messages for a user in a conversation
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND :userId NOT IN " +
           "(SELECT r.id FROM m.readBy r)")
    Long countUnreadMessagesForUser(@Param("conversationId") Long conversationId, 
                                   @Param("userId") Long userId);
    
    // Find last message in a conversation
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC")
    List<Message> findLastMessageInConversation(@Param("conversationId") Long conversationId, Pageable pageable);
} 