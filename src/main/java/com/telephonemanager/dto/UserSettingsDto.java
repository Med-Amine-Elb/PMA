package com.telephonemanager.dto;

import java.time.LocalDateTime;

public class UserSettingsDto {
    
    private Long id;
    private Long userId;
    private String theme;
    private String language;
    private String timezone;
    private String dateFormat;
    private String timeFormat;
    private boolean notificationsEnabled;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean pushNotifications;
    private Integer autoRefreshInterval;
    private Integer pageSize;
    private boolean showHelpTooltips;
    private boolean compactMode;
    private boolean sidebarCollapsed;
    private String dashboardLayout;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserSettingsDto() {}
    
    public UserSettingsDto(Long id, Long userId, String theme, String language, String timezone, 
                          String dateFormat, String timeFormat, boolean notificationsEnabled,
                          boolean emailNotifications, boolean smsNotifications, boolean pushNotifications,
                          Integer autoRefreshInterval, Integer pageSize, boolean showHelpTooltips,
                          boolean compactMode, boolean sidebarCollapsed, String dashboardLayout,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.theme = theme;
        this.language = language;
        this.timezone = timezone;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.notificationsEnabled = notificationsEnabled;
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
        this.pushNotifications = pushNotifications;
        this.autoRefreshInterval = autoRefreshInterval;
        this.pageSize = pageSize;
        this.showHelpTooltips = showHelpTooltips;
        this.compactMode = compactMode;
        this.sidebarCollapsed = sidebarCollapsed;
        this.dashboardLayout = dashboardLayout;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public String getTimeFormat() {
        return timeFormat;
    }
    
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
    
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    public boolean isEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public boolean isSmsNotifications() {
        return smsNotifications;
    }
    
    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    
    public boolean isPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Integer getAutoRefreshInterval() {
        return autoRefreshInterval;
    }
    
    public void setAutoRefreshInterval(Integer autoRefreshInterval) {
        this.autoRefreshInterval = autoRefreshInterval;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public boolean isShowHelpTooltips() {
        return showHelpTooltips;
    }
    
    public void setShowHelpTooltips(boolean showHelpTooltips) {
        this.showHelpTooltips = showHelpTooltips;
    }
    
    public boolean isCompactMode() {
        return compactMode;
    }
    
    public void setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
    }
    
    public boolean isSidebarCollapsed() {
        return sidebarCollapsed;
    }
    
    public void setSidebarCollapsed(boolean sidebarCollapsed) {
        this.sidebarCollapsed = sidebarCollapsed;
    }
    
    public String getDashboardLayout() {
        return dashboardLayout;
    }
    
    public void setDashboardLayout(String dashboardLayout) {
        this.dashboardLayout = dashboardLayout;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 