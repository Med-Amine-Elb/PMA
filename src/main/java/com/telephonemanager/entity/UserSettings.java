package com.telephonemanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "theme", nullable = false)
    private String theme = "light";
    
    @Column(name = "language", nullable = false)
    private String language = "en";
    
    @Column(name = "timezone", nullable = false)
    private String timezone = "UTC";
    
    @Column(name = "date_format", nullable = false)
    private String dateFormat = "yyyy-MM-dd";
    
    @Column(name = "time_format", nullable = false)
    private String timeFormat = "HH:mm";
    
    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;
    
    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;
    
    @Column(name = "sms_notifications", nullable = false)
    private boolean smsNotifications = false;
    
    @Column(name = "push_notifications", nullable = false)
    private boolean pushNotifications = true;
    
    @Column(name = "auto_refresh_interval")
    private Integer autoRefreshInterval = 30; // seconds
    
    @Column(name = "page_size", nullable = false)
    private Integer pageSize = 10;
    
    @Column(name = "show_help_tooltips", nullable = false)
    private boolean showHelpTooltips = true;
    
    @Column(name = "compact_mode", nullable = false)
    private boolean compactMode = false;
    
    @Column(name = "sidebar_collapsed", nullable = false)
    private boolean sidebarCollapsed = false;
    
    @Column(name = "dashboard_layout")
    private String dashboardLayout = "default";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public UserSettings() {}
    
    public UserSettings(User user) {
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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