package com.telephonemanager.repository;

import com.telephonemanager.entity.User;
import com.telephonemanager.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    
    // Find settings by user
    Optional<UserSettings> findByUser(User user);
    
    // Find settings by user ID
    @Query("SELECT us FROM UserSettings us WHERE us.user.id = :userId")
    Optional<UserSettings> findByUserId(@Param("userId") Long userId);
    
    // Check if settings exist for user
    boolean existsByUser(User user);
    
    // Check if settings exist for user ID
    @Query("SELECT COUNT(us) > 0 FROM UserSettings us WHERE us.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
} 