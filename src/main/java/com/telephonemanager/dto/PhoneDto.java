package com.telephonemanager.dto;

import java.time.LocalDate;

import com.telephonemanager.entity.Phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PhoneDto {
    private Long id;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotBlank
    private String imei1;
    private String imei2;
    private String serialNumber;
    @NotNull
    private Phone.Status status;
    @NotNull
    private Phone.Condition condition;
    private String storage;
    private String color;
    private Double price;
    private Long assignedToId;
    private String assignedToName;
    private String assignedToDepartment;
    private LocalDate assignedDate;
    private LocalDate purchaseDate;
    private Double remainingValue;
    private String notes;

    public PhoneDto() {
    }

    public PhoneDto(Phone phone) {
        this.id = phone.getId();
        this.brand = phone.getBrand();
        this.model = phone.getModel();
        this.imei1 = phone.getImei1();
        this.imei2 = phone.getImei2();
        this.serialNumber = phone.getSerialNumber();
        this.status = phone.getStatus();
        this.condition = phone.getCondition();
        this.storage = phone.getStorage();
        this.color = phone.getColor();
        this.price = phone.getPrice();
        if (phone.getAssignedTo() != null) {
            this.assignedToId = phone.getAssignedTo().getId();
            this.assignedToName = phone.getAssignedTo().getName();
            this.assignedToDepartment = phone.getAssignedTo().getDepartment();
        }
        this.assignedDate = phone.getAssignedDate();
        this.purchaseDate = phone.getPurchaseDate();
        // remainingValue is set manually in service
        this.notes = phone.getNotes();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImei1() {
        return imei1;
    }

    public void setImei1(String imei1) {
        this.imei1 = imei1;
    }

    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Phone.Status getStatus() {
        return status;
    }

    public void setStatus(Phone.Status status) {
        this.status = status;
    }

    public Phone.Condition getCondition() {
        return condition;
    }

    public void setCondition(Phone.Condition condition) {
        this.condition = condition;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public String getAssignedToDepartment() {
        return assignedToDepartment;
    }

    public void setAssignedToDepartment(String assignedToDepartment) {
        this.assignedToDepartment = assignedToDepartment;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getRemainingValue() {
        return remainingValue;
    }

    public void setRemainingValue(Double remainingValue) {
        this.remainingValue = remainingValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}