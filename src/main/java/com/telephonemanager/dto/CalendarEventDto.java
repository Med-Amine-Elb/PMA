package com.telephonemanager.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class CalendarEventDto {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String type;
    private String status;
    private Long organizerId;
    private String organizerName;
    private Set<Long> attendeeIds;
    private Set<String> attendeeNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String color;
    private boolean isAllDay;
    private String recurrence;
    
    // Constructors
    public CalendarEventDto() {}
    
    public CalendarEventDto(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, 
                           String location, String type, String status, Long organizerId, String organizerName,
                           Set<Long> attendeeIds, Set<String> attendeeNames, LocalDateTime createdAt, 
                           LocalDateTime updatedAt, String color, boolean isAllDay, String recurrence) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.type = type;
        this.status = status;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.attendeeIds = attendeeIds;
        this.attendeeNames = attendeeNames;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.color = color;
        this.isAllDay = isAllDay;
        this.recurrence = recurrence;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getOrganizerId() {
        return organizerId;
    }
    
    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
    
    public String getOrganizerName() {
        return organizerName;
    }
    
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
    
    public Set<Long> getAttendeeIds() {
        return attendeeIds;
    }
    
    public void setAttendeeIds(Set<Long> attendeeIds) {
        this.attendeeIds = attendeeIds;
    }
    
    public Set<String> getAttendeeNames() {
        return attendeeNames;
    }
    
    public void setAttendeeNames(Set<String> attendeeNames) {
        this.attendeeNames = attendeeNames;
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
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public boolean isAllDay() {
        return isAllDay;
    }
    
    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }
    
    public String getRecurrence() {
        return recurrence;
    }
    
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
} 