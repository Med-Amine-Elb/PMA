package com.telephonemanager.dto;

import java.time.LocalDate;

import com.telephonemanager.entity.SimCard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SimCardDto {
    private Long id;
    @NotBlank
    private String number;
    @NotBlank
    private String iccid;
    @NotNull
    private SimCard.Status status;
    private Long assignedToId;
    private String assignedToName;
    private LocalDate assignedDate;
    private String notes;
    @NotBlank
    private String pin;
    @NotBlank
    private String puk;
    private String poke; // Made optional since frontend doesn't send it
    
    // New fields for frontend compatibility
    @NotBlank
    private String carrier;
    @NotBlank
    private String plan;
    private Double monthlyFee;
    private String dataLimit;
    private LocalDate activationDate; // Added missing field
    private LocalDate expiryDate;

    public SimCardDto() {}
    public SimCardDto(SimCard sim) {
        this.id = sim.getId();
        this.number = sim.getNumber();
        this.iccid = sim.getIccid();
        this.status = sim.getStatus();
        if (sim.getAssignedTo() != null) {
            this.assignedToId = sim.getAssignedTo().getId();
            this.assignedToName = sim.getAssignedTo().getName();
        }
        this.assignedDate = sim.getAssignedDate();
        this.notes = sim.getNotes();
        this.pin = sim.getPin();
        this.puk = sim.getPuk();
        this.poke = sim.getPoke();
        this.carrier = sim.getCarrier();
        this.plan = sim.getPlan();
        this.monthlyFee = sim.getMonthlyFee();
        this.dataLimit = sim.getDataLimit();
        this.activationDate = sim.getActivationDate();
        this.expiryDate = sim.getExpiryDate();
    }
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public SimCard.Status getStatus() { return status; }
    public void setStatus(SimCard.Status status) { this.status = status; }
    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public String getPuk() { return puk; }
    public void setPuk(String puk) { this.puk = puk; }
    public String getPoke() { return poke; }
    public void setPoke(String poke) { this.poke = poke; }
    
    // New getters and setters
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public Double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(Double monthlyFee) { this.monthlyFee = monthlyFee; }
    public String getDataLimit() { return dataLimit; }
    public void setDataLimit(String dataLimit) { this.dataLimit = dataLimit; }
    public LocalDate getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDate activationDate) { this.activationDate = activationDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
} 