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
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC")
    Page<Message> findByConversationOrderBySentAtDesc(@Param("conversationId") Long conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt ASC")
    List<Message> findByConversationOrderBySentAtAsc(@Param("conversationId") Long conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.sentAt DESC")
    Page<Message> findBySenderOrderBySentAtDesc(@Param("senderId") Long senderId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.sentAt > :after ORDER BY m.sentAt ASC")
    List<Message> findByConversationAndSentAtAfter(@Param("conversationId") Long conversationId, @Param("after") LocalDateTime after);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.id > :messageId ORDER BY m.sentAt ASC")
    List<Message> findByConversationAndIdGreaterThan(@Param("conversationId") Long conversationId, @Param("messageId") Long messageId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.type = :type ORDER BY m.sentAt DESC")
    Page<Message> findByConversationAndType(@Param("conversationId") Long conversationId, @Param("type") String type, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.content LIKE %:searchTerm% ORDER BY m.sentAt DESC")
    Page<Message> findByConversationAndContentContaining(@Param("conversationId") Long conversationId, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.sender.id != :userId
          AND NOT EXISTS (
            SELECT u FROM m.readBy u WHERE u.id = :userId
          )
    """)
    long countUnreadMessagesForUser(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.sender.id != :userId
          AND NOT EXISTS (
            SELECT u FROM m.readBy u WHERE u.id = :userId
          )
        ORDER BY m.sentAt ASC
    """)
    List<Message> findUnreadMessagesForUser(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.isStarred = true ORDER BY m.sentAt DESC")
    Page<Message> findStarredMessagesByConversation(@Param("conversationId") Long conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC LIMIT 1")
    Message findLastMessageByConversation(@Param("conversationId") Long conversationId);
} 