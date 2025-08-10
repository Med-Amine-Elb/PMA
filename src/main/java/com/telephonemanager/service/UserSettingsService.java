package com.telephonemanager.service;

import com.telephonemanager.dto.UserSettingsDto;
import com.telephonemanager.entity.User;
import com.telephonemanager.entity.UserSettings;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserSettingsService {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public UserSettingsDto getUserSettings(Long userId) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return convertToDto(settings.get());
        }
        
        // Create default settings if none exist
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserSettings defaultSettings = new UserSettings(user.get());
            UserSettings savedSettings = userSettingsRepository.save(defaultSettings);
            return convertToDto(savedSettings);
        }
        
        return null;
    }
    
    public UserSettingsDto updateUserSettings(Long userId, UserSettingsDto settingsDto) {
        Optional<UserSettings> existingSettings = userSettingsRepository.findByUserId(userId);
        
        if (existingSettings.isPresent()) {
            UserSettings settings = existingSettings.get();
            updateSettingsFromDto(settings, settingsDto);
            UserSettings savedSettings = userSettingsRepository.save(settings);
            return convertToDto(savedSettings);
        } else {
            // Create new settings if none exist
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                UserSettings newSettings = new UserSettings(user.get());
                updateSettingsFromDto(newSettings, settingsDto);
                UserSettings savedSettings = userSettingsRepository.save(newSettings);
                return convertToDto(savedSettings);
            }
        }
        
        return null;
    }
    
    public boolean deleteUserSettings(Long userId) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            userSettingsRepository.delete(settings.get());
            return true;
        }
        return false;
    }
    
    public UserSettingsDto resetToDefaults(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            // Delete existing settings
            userSettingsRepository.findByUserId(userId).ifPresent(userSettingsRepository::delete);
            
            // Create default settings
            UserSettings defaultSettings = new UserSettings(user.get());
            UserSettings savedSettings = userSettingsRepository.save(defaultSettings);
            return convertToDto(savedSettings);
        }
        return null;
    }
    
    public boolean updateTheme(Long userId, String theme) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setTheme(theme);
            userSettingsRepository.save(userSettings);
            return true;
        }
        return false;
    }
    
    public boolean updateLanguage(Long userId, String language) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setLanguage(language);
            userSettingsRepository.save(userSettings);
            return true;
        }
        return false;
    }
    
    public boolean updateTimezone(Long userId, String timezone) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setTimezone(timezone);
            userSettingsRepository.save(userSettings);
            return true;
        }
        return false;
    }
    
    public boolean updateNotificationSettings(Long userId, boolean notificationsEnabled, 
                                            boolean emailNotifications, boolean smsNotifications, 
                                            boolean pushNotifications) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setNotificationsEnabled(notificationsEnabled);
            userSettings.setEmailNotifications(emailNotifications);
            userSettings.setSmsNotifications(smsNotifications);
            userSettings.setPushNotifications(pushNotifications);
            userSettingsRepository.save(userSettings);
            return true;
        }
        return false;
    }
    
    public boolean updatePageSize(Long userId, Integer pageSize) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setPageSize(pageSize);
            userSettingsRepository.save(userSettings);
            return true;
        }
        return false;
    }
    
    private UserSettingsDto convertToDto(UserSettings settings) {
        UserSettingsDto dto = new UserSettingsDto();
        dto.setId(settings.getId());
        dto.setUserId(settings.getUser().getId());
        dto.setTheme(settings.getTheme());
        dto.setLanguage(settings.getLanguage());
        dto.setTimezone(settings.getTimezone());
        dto.setDateFormat(settings.getDateFormat());
        dto.setTimeFormat(settings.getTimeFormat());
        dto.setNotificationsEnabled(settings.isNotificationsEnabled());
        dto.setEmailNotifications(settings.isEmailNotifications());
        dto.setSmsNotifications(settings.isSmsNotifications());
        dto.setPushNotifications(settings.isPushNotifications());
        dto.setAutoRefreshInterval(settings.getAutoRefreshInterval());
        dto.setPageSize(settings.getPageSize());
        dto.setShowHelpTooltips(settings.isShowHelpTooltips());
        dto.setCompactMode(settings.isCompactMode());
        dto.setSidebarCollapsed(settings.isSidebarCollapsed());
        dto.setDashboardLayout(settings.getDashboardLayout());
        dto.setCreatedAt(settings.getCreatedAt());
        dto.setUpdatedAt(settings.getUpdatedAt());
        return dto;
    }
    
    private void updateSettingsFromDto(UserSettings settings, UserSettingsDto dto) {
        if (dto.getTheme() != null) settings.setTheme(dto.getTheme());
        if (dto.getLanguage() != null) settings.setLanguage(dto.getLanguage());
        if (dto.getTimezone() != null) settings.setTimezone(dto.getTimezone());
        if (dto.getDateFormat() != null) settings.setDateFormat(dto.getDateFormat());
        if (dto.getTimeFormat() != null) settings.setTimeFormat(dto.getTimeFormat());
        settings.setNotificationsEnabled(dto.isNotificationsEnabled());
        settings.setEmailNotifications(dto.isEmailNotifications());
        settings.setSmsNotifications(dto.isSmsNotifications());
        settings.setPushNotifications(dto.isPushNotifications());
        if (dto.getAutoRefreshInterval() != null) settings.setAutoRefreshInterval(dto.getAutoRefreshInterval());
        if (dto.getPageSize() != null) settings.setPageSize(dto.getPageSize());
        settings.setShowHelpTooltips(dto.isShowHelpTooltips());
        settings.setCompactMode(dto.isCompactMode());
        settings.setSidebarCollapsed(dto.isSidebarCollapsed());
        if (dto.getDashboardLayout() != null) settings.setDashboardLayout(dto.getDashboardLayout());
    }
} 