package com.telephonemanager.dto;

import com.telephonemanager.entity.Attribution;
import com.telephonemanager.entity.Attribution.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttributionDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long phoneId;
    private String phoneModel;
    private String phoneBrand;
    private Long simCardId;
    private String simCardNumber;
    private Long assignedById;
    private String assignedByName;
    private LocalDate assignmentDate;
    private LocalDate returnDate;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AttributionDto() {}

    public AttributionDto(Attribution attribution) {
        this.id = attribution.getId();
        this.userId = attribution.getUser().getId();
        this.userName = attribution.getUser().getName();
        this.userEmail = attribution.getUser().getEmail();
        
        if (attribution.getPhone() != null) {
            this.phoneId = attribution.getPhone().getId();
            this.phoneModel = attribution.getPhone().getModel();
            this.phoneBrand = attribution.getPhone().getBrand();
        }
        
        if (attribution.getSimCard() != null) {
            this.simCardId = attribution.getSimCard().getId();
            this.simCardNumber = attribution.getSimCard().getNumber();
        }
        
        this.assignedById = attribution.getAssignedBy().getId();
        this.assignedByName = attribution.getAssignedBy().getName();
        this.assignmentDate = attribution.getAssignmentDate();
        this.returnDate = attribution.getReturnDate();
        this.status = attribution.getStatus();
        this.notes = attribution.getNotes();
        this.createdAt = attribution.getCreatedAt();
        this.updatedAt = attribution.getUpdatedAt();
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

    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public Long getSimCardId() {
        return simCardId;
    }

    public void setSimCardId(Long simCardId) {
        this.simCardId = simCardId;
    }

    public String getSimCardNumber() {
        return simCardNumber;
    }

    public void setSimCardNumber(String simCardNumber) {
        this.simCardNumber = simCardNumber;
    }

    public Long getAssignedById() {
        return assignedById;
    }

    public void setAssignedById(Long assignedById) {
        this.assignedById = assignedById;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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