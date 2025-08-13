package com.telephonemanager.repository;

import com.telephonemanager.entity.SimCard;
import com.telephonemanager.entity.SimCard.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SimCardRepository extends JpaRepository<SimCard, Long> {
    Optional<SimCard> findByIccid(String iccid);
    Optional<SimCard> findByPuk(String puk);

    @Query("SELECT s FROM SimCard s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:number IS NULL OR s.number LIKE CONCAT('%', :number, '%')) AND " +
           "(:iccid IS NULL OR s.iccid LIKE CONCAT('%', :iccid, '%'))")
    Page<SimCard> findSimCardsWithFilters(@Param("status") Status status,
                                          @Param("number") String number,
                                          @Param("iccid") String iccid,
                                          Pageable pageable);

    // Dashboard methods
    long countByAssignedToIsNotNull();
    long countByStatus(Status status);
    List<SimCard> findByStatusAndAssignedDateBefore(Status status, LocalDate date);
    
    // Department-based queries - will be implemented when relationships are established
} 