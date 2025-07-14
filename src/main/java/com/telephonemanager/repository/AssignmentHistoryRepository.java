package com.telephonemanager.repository;

import com.telephonemanager.entity.AssignmentHistory;
import com.telephonemanager.entity.AssignmentHistory.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, Long> {
    List<AssignmentHistory> findByTypeAndItemId(Type type, Long itemId);
    List<AssignmentHistory> findByToUserIdOrFromUserId(Long toUserId, Long fromUserId);
    
    // Dashboard methods
    long countByDateAfter(LocalDateTime date);
    List<AssignmentHistory> findTop10ByOrderByDateDesc();
} 