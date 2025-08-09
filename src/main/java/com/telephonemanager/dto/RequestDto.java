package com.telephonemanager.dto;

import com.telephonemanager.entity.Request;
import com.telephonemanager.entity.Request.Priority;
import com.telephonemanager.entity.Request.Status;
import com.telephonemanager.entity.Request.Type;

import java.time.LocalDateTime;

public class RequestDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Type type;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolution;

    // Constructors
    public RequestDto() {}

    public RequestDto(Request request) {
        this.id = request.getId();
        this.userId = request.getUser().getId();
        this.userName = request.getUser().getName();
        this.userEmail = request.getUser().getEmail();
        this.type = request.getType();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.status = request.getStatus();
        this.priority = request.getPriority();
        
        if (request.getAssignedTo() != null) {
            this.assignedToId = request.getAssignedTo().getId();
            this.assignedToName = request.getAssignedTo().getName();
        }
        
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();
        this.resolvedAt = request.getResolvedAt();
        this.resolution = request.getResolution();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
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

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
} 