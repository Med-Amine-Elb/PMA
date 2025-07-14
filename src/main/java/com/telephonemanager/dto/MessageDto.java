package com.telephonemanager.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class MessageDto {
    
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;
    private LocalDateTime sentAt;
    private LocalDateTime editedAt;
    private boolean isEdited;
    private boolean isStarred;
    private Set<Long> readByUserIds;
    private Set<String> readByUserNames;
    private Long replyToId;
    private String attachmentUrl;
    private boolean isReadByCurrentUser;
    
    // Constructors
    public MessageDto() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public void setEdited(boolean edited) {
        isEdited = edited;
    }
    
    public boolean isStarred() {
        return isStarred;
    }
    
    public void setStarred(boolean starred) {
        isStarred = starred;
    }
    
    public Set<Long> getReadByUserIds() {
        return readByUserIds;
    }
    
    public void setReadByUserIds(Set<Long> readByUserIds) {
        this.readByUserIds = readByUserIds;
    }
    
    public Set<String> getReadByUserNames() {
        return readByUserNames;
    }
    
    public void setReadByUserNames(Set<String> readByUserNames) {
        this.readByUserNames = readByUserNames;
    }
    
    public Long getReplyToId() {
        return replyToId;
    }
    
    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
    
    public boolean isReadByCurrentUser() {
        return isReadByCurrentUser;
    }
    
    public void setReadByCurrentUser(boolean readByCurrentUser) {
        isReadByCurrentUser = readByCurrentUser;
    }
} 