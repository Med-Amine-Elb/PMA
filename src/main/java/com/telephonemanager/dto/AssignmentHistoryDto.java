package com.telephonemanager.dto;

import com.telephonemanager.entity.AssignmentHistory;
import java.time.LocalDateTime;

public class AssignmentHistoryDto {
    private Long id;
    private AssignmentHistory.Type type;
    private Long itemId;
    private Long fromUserId;
    private Long toUserId;
    private AssignmentHistory.Action action;
    private LocalDateTime date;
    private String notes;

    public AssignmentHistoryDto() {}
    public AssignmentHistoryDto(AssignmentHistory h) {
        this.id = h.getId();
        this.type = h.getType();
        this.itemId = h.getItemId();
        this.fromUserId = h.getFromUserId();
        this.toUserId = h.getToUserId();
        this.action = h.getAction();
        this.date = h.getDate();
        this.notes = h.getNotes();
    }
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AssignmentHistory.Type getType() { return type; }
    public void setType(AssignmentHistory.Type type) { this.type = type; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public AssignmentHistory.Action getAction() { return action; }
    public void setAction(AssignmentHistory.Action action) { this.action = action; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 