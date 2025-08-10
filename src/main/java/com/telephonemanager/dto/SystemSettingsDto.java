package com.telephonemanager.dto;

import java.time.LocalDateTime;

public class SystemSettingsDto {
    
    private Long id;
    private String key;
    private String value;
    private String description;
    private String category;
    private String dataType;
    private boolean isEncrypted;
    private boolean isEditable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public SystemSettingsDto() {}
    
    public SystemSettingsDto(Long id, String key, String value, String description, String category,
                           String dataType, boolean isEncrypted, boolean isEditable,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.description = description;
        this.category = category;
        this.dataType = dataType;
        this.isEncrypted = isEncrypted;
        this.isEditable = isEditable;
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
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public boolean isEncrypted() {
        return isEncrypted;
    }
    
    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
    
    public boolean isEditable() {
        return isEditable;
    }
    
    public void setEditable(boolean editable) {
        isEditable = editable;
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