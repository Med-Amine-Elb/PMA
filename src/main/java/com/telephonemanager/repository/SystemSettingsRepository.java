package com.telephonemanager.repository;

import com.telephonemanager.entity.SystemSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    
    // Find setting by key
    Optional<SystemSettings> findByKey(String key);
    
    // Find settings by category
    Page<SystemSettings> findByCategory(String category, Pageable pageable);
    
    // Find settings by category as list
    List<SystemSettings> findByCategory(String category);
    
    // Find settings by data type
    Page<SystemSettings> findByDataType(String dataType, Pageable pageable);
    
    // Find editable settings
    Page<SystemSettings> findByIsEditableTrue(Pageable pageable);
    
    // Find encrypted settings
    Page<SystemSettings> findByIsEncryptedTrue(Pageable pageable);
    
    // Find settings by key containing
    Page<SystemSettings> findByKeyContainingIgnoreCase(String key, Pageable pageable);
    
    // Find settings by description containing
    Page<SystemSettings> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    // Find settings by category and editable
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.category = :category AND ss.isEditable = :isEditable")
    Page<SystemSettings> findByCategoryAndEditable(@Param("category") String category, 
                                                  @Param("isEditable") boolean isEditable, 
                                                  Pageable pageable);
    
    // Check if setting exists by key
    boolean existsByKey(String key);
    
    // Get all categories
    @Query("SELECT DISTINCT ss.category FROM SystemSettings ss ORDER BY ss.category")
    List<String> findAllCategories();
} 