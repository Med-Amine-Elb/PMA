package com.telephonemanager.repository;

import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.Phone.Status;
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
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Optional<Phone> findByImei(String imei);

    @Query("SELECT p FROM Phone p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:brand IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
           "(:model IS NULL OR LOWER(p.model) LIKE LOWER(CONCAT('%', :model, '%')))")
    Page<Phone> findPhonesWithFilters(@Param("status") Status status,
                                      @Param("brand") String brand,
                                      @Param("model") String model,
                                      Pageable pageable);

    // Dashboard methods
    long countByAssignedToIsNotNull();
    long countByStatus(Status status);
    
    @Query("SELECT p.brand, COUNT(p) FROM Phone p GROUP BY p.brand")
    List<Object[]> findBrandDistribution();
    
    List<Phone> findByStatusAndAssignedDateBefore(Status status, LocalDate date);
} 