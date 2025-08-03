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
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId AND c.isActive = true ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findByParticipantsUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE c.id = :conversationId AND p.id = :userId AND c.isActive = true")
    Optional<Conversation> findByIdAndParticipantsUserId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.createdBy.id = :userId AND c.isActive = true ORDER BY c.createdAt DESC")
    List<Conversation> findByCreatedById(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.type = 'DIRECT' AND c.isActive = true AND " +
           "EXISTS (SELECT 1 FROM c.participants p1 WHERE p1.id = :userId1) AND " +
           "EXISTS (SELECT 1 FROM c.participants p2 WHERE p2.id = :userId2)")
    Optional<Conversation> findDirectConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT c FROM Conversation c WHERE c.title LIKE %:searchTerm% AND c.isActive = true")
    Page<Conversation> findByTitleContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT c FROM Conversation c WHERE c.type = :type AND c.isActive = true")
    List<Conversation> findByType(@Param("type") String type);
    
    @Query("SELECT COUNT(c) FROM Conversation c JOIN c.participants p WHERE p.id = :userId AND c.isActive = true")
    long countByParticipantsUserId(@Param("userId") Long userId);
} 