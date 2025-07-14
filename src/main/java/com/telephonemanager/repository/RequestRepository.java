package com.telephonemanager.repository;

import com.telephonemanager.entity.Request;
import com.telephonemanager.entity.Request.Priority;
import com.telephonemanager.entity.Request.Status;
import com.telephonemanager.entity.Request.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    
    // Find by user
    Page<Request> findByUserId(Long userId, Pageable pageable);
    
    // Find by status
    Page<Request> findByStatus(Status status, Pageable pageable);
    
    // Find by type
    Page<Request> findByType(Type type, Pageable pageable);
    
    // Find by priority
    Page<Request> findByPriority(Priority priority, Pageable pageable);
    
    // Find by assigned to
    Page<Request> findByAssignedToId(Long assignedToId, Pageable pageable);
    
    // Find by user and status
    Page<Request> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);
    
    // Find by user and type
    Page<Request> findByUserIdAndType(Long userId, Type type, Pageable pageable);
    
    // Search requests
    @Query("SELECT r FROM Request r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:type IS NULL OR r.type = :type) AND " +
           "(:priority IS NULL OR r.priority = :priority) AND " +
           "(:assignedToId IS NULL OR r.assignedTo.id = :assignedToId) AND " +
           "(:search IS NULL OR " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.user.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Request> findWithFilters(
        @Param("userId") Long userId,
        @Param("status") Status status,
        @Param("type") Type type,
        @Param("priority") Priority priority,
        @Param("assignedToId") Long assignedToId,
        @Param("search") String search,
        Pageable pageable
    );
    
    // Count by status
    long countByStatus(Status status);
    
    // Count by user and status
    long countByUserIdAndStatus(Long userId, Status status);
    
    // Count by priority
    long countByPriority(Priority priority);
    
    // Find urgent requests
    List<Request> findByPriorityOrderByCreatedAtDesc(Priority priority);
    
    // Find pending requests
    List<Request> findByStatusOrderByCreatedAtDesc(Status status);
    
    // Find requests by user
    List<Request> findByUserIdOrderByCreatedAtDesc(Long userId);
} 