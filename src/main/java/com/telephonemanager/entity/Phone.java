package com.telephonemanager.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, unique = true)
    private String imei;

    @Column
    private String serialNumber;  // N° SÉRIE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condition condition = Condition.EXCELLENT;  // ÉTAT

    @Column
    private String storage;  // STOCKAGE

    @Column
    private String color;  // COULEUR

    @Column
    private Double price;  // PRIX

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    private LocalDate assignedDate;

    @Column
    private LocalDate purchaseDate;  // DATE D'ACHAT

    private String notes;

    public enum Status {
        AVAILABLE,
        ASSIGNED,
        LOST,
        DAMAGED
    }

    public enum Condition {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    
        public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 