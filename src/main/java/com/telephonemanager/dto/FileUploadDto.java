package com.telephonemanager.dto;

import java.time.LocalDateTime;

public class FileUploadDto {
    
    private Long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String fileExtension;
    private String uploadType;
    private Long uploadedById;
    private String uploadedByName;
    private String relatedEntityType;
    private Long relatedEntityId;
    private String description;
    private boolean isPublic;
    private Integer downloadCount;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private String downloadUrl;
    
    // Constructors
    public FileUploadDto() {}
    
    public FileUploadDto(Long id, String fileName, String originalName, String filePath, Long fileSize,
                        String contentType, String fileExtension, String uploadType, Long uploadedById,
                        String uploadedByName, String relatedEntityType, Long relatedEntityId,
                        String description, boolean isPublic, Integer downloadCount,
                        LocalDateTime uploadedAt, LocalDateTime updatedAt, String downloadUrl) {
        this.id = id;
        this.fileName = fileName;
        this.originalName = originalName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
        this.uploadType = uploadType;
        this.uploadedById = uploadedById;
        this.uploadedByName = uploadedByName;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.description = description;
        this.isPublic = isPublic;
        this.downloadCount = downloadCount;
        this.uploadedAt = uploadedAt;
        this.updatedAt = updatedAt;
        this.downloadUrl = downloadUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    
    public String getUploadType() {
        return uploadType;
    }
    
    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }
    
    public Long getUploadedById() {
        return uploadedById;
    }
    
    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }
    
    public String getUploadedByName() {
        return uploadedByName;
    }
    
    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }
    
    public String getRelatedEntityType() {
        return relatedEntityType;
    }
    
    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }
    
    public Long getRelatedEntityId() {
        return relatedEntityId;
    }
    
    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    public Integer getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
} 