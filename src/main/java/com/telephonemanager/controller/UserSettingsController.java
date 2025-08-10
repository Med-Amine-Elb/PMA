package com.telephonemanager.controller;

import com.telephonemanager.dto.UserSettingsDto;
import com.telephonemanager.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings/user")
@CrossOrigin(origins = "*")
public class UserSettingsController {
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    // Get user settings
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<UserSettingsDto> getUserSettings(@PathVariable Long userId) {
        UserSettingsDto settings = userSettingsService.getUserSettings(userId);
        return settings != null ? ResponseEntity.ok(settings) : ResponseEntity.notFound().build();
    }
    
    // Update user settings
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<UserSettingsDto> updateUserSettings(
            @PathVariable Long userId, 
            @RequestBody UserSettingsDto settingsDto) {
        UserSettingsDto updatedSettings = userSettingsService.updateUserSettings(userId, settingsDto);
        return updatedSettings != null ? ResponseEntity.ok(updatedSettings) : ResponseEntity.notFound().build();
    }
    
    // Delete user settings
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Void> deleteUserSettings(@PathVariable Long userId) {
        boolean deleted = userSettingsService.deleteUserSettings(userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Reset user settings to defaults
    @PostMapping("/{userId}/reset")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<UserSettingsDto> resetToDefaults(@PathVariable Long userId) {
        UserSettingsDto settings = userSettingsService.resetToDefaults(userId);
        return settings != null ? ResponseEntity.ok(settings) : ResponseEntity.notFound().build();
    }
    
    // Update theme
    @PutMapping("/{userId}/theme")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> updateTheme(
            @PathVariable Long userId, 
            @RequestParam String theme) {
        boolean updated = userSettingsService.updateTheme(userId, theme);
        return ResponseEntity.ok(updated);
    }
    
    // Update language
    @PutMapping("/{userId}/language")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> updateLanguage(
            @PathVariable Long userId, 
            @RequestParam String language) {
        boolean updated = userSettingsService.updateLanguage(userId, language);
        return ResponseEntity.ok(updated);
    }
    
    // Update timezone
    @PutMapping("/{userId}/timezone")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> updateTimezone(
            @PathVariable Long userId, 
            @RequestParam String timezone) {
        boolean updated = userSettingsService.updateTimezone(userId, timezone);
        return ResponseEntity.ok(updated);
    }
    
    // Update notification settings
    @PutMapping("/{userId}/notifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> updateNotificationSettings(
            @PathVariable Long userId,
            @RequestParam boolean notificationsEnabled,
            @RequestParam boolean emailNotifications,
            @RequestParam boolean smsNotifications,
            @RequestParam boolean pushNotifications) {
        boolean updated = userSettingsService.updateNotificationSettings(
            userId, notificationsEnabled, emailNotifications, smsNotifications, pushNotifications);
        return ResponseEntity.ok(updated);
    }
    
    // Update page size
    @PutMapping("/{userId}/page-size")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Boolean> updatePageSize(
            @PathVariable Long userId, 
            @RequestParam Integer pageSize) {
        boolean updated = userSettingsService.updatePageSize(userId, pageSize);
        return ResponseEntity.ok(updated);
    }
} 