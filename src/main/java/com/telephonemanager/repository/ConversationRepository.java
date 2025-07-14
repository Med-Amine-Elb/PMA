package com.telephonemanager.repository;

import com.telephonemanager.entity.Conversation;
import com.telephonemanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    // Find conversations where user is a participant
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId AND c.isActive = true")
    Page<Conversation> findByParticipantId(@Param("userId") Long userId, Pageable pageable);
    
    // Find conversations by type
    Page<Conversation> findByType(Conversation.ConversationType type, Pageable pageable);
    
    // Find conversations created by user
    Page<Conversation> findByCreatedBy(User createdBy, Pageable pageable);
    
    // Find direct conversation between two users
    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE c.type = 'DIRECT' AND p1.id = :userId1 AND p2.id = :userId2 AND c.isActive = true")
    List<Conversation> findDirectConversationBetweenUsers(@Param("userId1") Long userId1, 
                                                         @Param("userId2") Long userId2);
    
    // Find conversations by title containing
    Page<Conversation> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    // Find active conversations
    Page<Conversation> findByIsActiveTrue(Pageable pageable);
    
    // Find conversations with recent activity
    @Query("SELECT c FROM Conversation c WHERE c.lastMessageAt IS NOT NULL ORDER BY c.lastMessageAt DESC")
    Page<Conversation> findConversationsWithRecentActivity(Pageable pageable);
    
    // Find conversations where user is participant and has unread messages
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.participants p " +
           "WHERE p.id = :userId AND c.isActive = true AND EXISTS " +
           "(SELECT m FROM Message m WHERE m.conversation = c AND :userId NOT IN " +
           "(SELECT r.id FROM m.readBy r))")
    List<Conversation> findConversationsWithUnreadMessages(@Param("userId") Long userId);
} 