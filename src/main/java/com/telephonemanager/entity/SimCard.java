package com.telephonemanager.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SimCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false, unique = true)
    private String iccid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    private LocalDate assignedDate;

    private String notes;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false, unique = true)
    private String puk;

    @Column(nullable = false)
    private String poke;

    public enum Status {
        AVAILABLE,
        ASSIGNED,
        LOST,
        BLOCKED
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
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
} 