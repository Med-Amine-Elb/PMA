package com.telephonemanager.repository;

import com.telephonemanager.entity.Attribution;
import com.telephonemanager.entity.Attribution.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface AttributionRepository extends JpaRepository<Attribution, Long> {
    
    // Find by user
    Page<Attribution> findByUserId(Long userId, Pageable pageable);
    
    // Find by status
    Page<Attribution> findByStatus(Status status, Pageable pageable);
    
    // Find by assigned by
    Page<Attribution> findByAssignedById(Long assignedById, Pageable pageable);
    
    // Find by user and status
    Page<Attribution> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);
    
    // Find active attributions for a user
    List<Attribution> findByUserIdAndStatus(Long userId, Status status);
    
    // Find by phone
    Optional<Attribution> findByPhoneIdAndStatus(Long phoneId, Status status);
    
    // Find by SIM card
    Optional<Attribution> findBySimCardIdAndStatus(Long simCardId, Status status);
    
    // Search attributions
    @Query("SELECT a FROM Attribution a WHERE " +
           "(:userId IS NULL OR a.user.id = :userId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:assignedById IS NULL OR a.assignedBy.id = :assignedById) AND " +
           "(:search IS NULL OR " +
           "LOWER(a.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.notes) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Attribution> findWithFilters(
        @Param("userId") Long userId,
        @Param("status") Status status,
        @Param("assignedById") Long assignedById,
        @Param("search") String search,
        Pageable pageable
    );
    
    // Count active attributions by user
    long countByUserIdAndStatus(Long userId, Status status);
    
    // Count active attributions by phone
    long countByPhoneIdAndStatus(Long phoneId, Status status);
    
    // Count active attributions by SIM card
    long countBySimCardIdAndStatus(Long simCardId, Status status);
    
    // Find attribution history for a SIM card
    @Query("SELECT a FROM Attribution a WHERE a.simCard.id = :simCardId ORDER BY a.assignmentDate DESC")
    List<Attribution> findHistoryBySimCardId(@Param("simCardId") Long simCardId);
    
    // Find attribution history for a phone
    @Query("SELECT a FROM Attribution a WHERE a.phone.id = :phoneId ORDER BY a.assignmentDate DESC")
    List<Attribution> findHistoryByPhoneId(@Param("phoneId") Long phoneId);

    // Direct field update to ensure persistence of date/status/notes
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Attribution a SET a.assignmentDate = :assignmentDate, a.returnDate = :returnDate, a.status = :status, a.notes = :notes WHERE a.id = :id")
    int updateCoreFields(
        @Param("id") Long id,
        @Param("assignmentDate") LocalDate assignmentDate,
        @Param("returnDate") LocalDate returnDate,
        @Param("status") Status status,
        @Param("notes") String notes
    );
} 