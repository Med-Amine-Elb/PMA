package com.telephonemanager.controller;

import com.telephonemanager.dto.FileUploadDto;
import com.telephonemanager.entity.FileUpload;
import com.telephonemanager.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    // Upload file
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<FileUploadDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedById") Long uploadedById,
            @RequestParam("uploadType") FileUpload.UploadType uploadType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "relatedEntityType", required = false) String relatedEntityType,
            @RequestParam(value = "relatedEntityId", required = false) Long relatedEntityId,
            @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic) {
        
        try {
            FileUploadDto uploadedFile = fileUploadService.uploadFile(
                file, uploadedById, uploadType, description, relatedEntityType, relatedEntityId, isPublic);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Download file
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Resource resource = fileUploadService.downloadFile(id);
            FileUploadDto fileInfo = fileUploadService.getFileById(id);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get file info by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<FileUploadDto> getFileById(@PathVariable Long id) {
        FileUploadDto file = fileUploadService.getFileById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }
    
    // Get all files with pagination
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> getAllFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FileUploadDto> files = fileUploadService.getAllFiles(pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by upload type
    @GetMapping("/type/{uploadType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesByUploadType(
            @PathVariable FileUpload.UploadType uploadType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesByUploadType(uploadType, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by user
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesByUser(userId, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by related entity
    @GetMapping("/related/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesByRelatedEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesByRelatedEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get public files
    @GetMapping("/public")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Page<FileUploadDto>> getPublicFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getPublicFiles(pageable);
        return ResponseEntity.ok(files);
    }
    
    // Search files by file name
    @GetMapping("/search/filename")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> searchFilesByFileName(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.searchFilesByFileName(fileName, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Search files by original name
    @GetMapping("/search/originalname")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> searchFilesByOriginalName(
            @RequestParam String originalName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.searchFilesByOriginalName(originalName, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Search files by description
    @GetMapping("/search/description")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> searchFilesByDescription(
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.searchFilesByDescription(description, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by content type
    @GetMapping("/content-type/{contentType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesByContentType(
            @PathVariable String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesByContentType(contentType, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by file extension
    @GetMapping("/extension/{fileExtension}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesByFileExtension(
            @PathVariable String fileExtension,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesByFileExtension(fileExtension, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Get files by size range
    @GetMapping("/size-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Page<FileUploadDto>> getFilesBySizeRange(
            @RequestParam Long minSize,
            @RequestParam Long maxSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileUploadDto> files = fileUploadService.getFilesBySizeRange(minSize, maxSize, pageable);
        return ResponseEntity.ok(files);
    }
    
    // Delete file
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        boolean deleted = fileUploadService.deleteFile(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Update file metadata
    @PutMapping("/{id}/metadata")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<FileUploadDto> updateFileMetadata(
            @PathVariable Long id,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic) {
        
        FileUploadDto updatedFile = fileUploadService.updateFileMetadata(id, description, isPublic != null ? isPublic : false);
        return updatedFile != null ? ResponseEntity.ok(updatedFile) : ResponseEntity.notFound().build();
    }
    
    // Get file statistics
    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<Object> getFileStatsByUser(@PathVariable Long userId) {
        Long totalSize = fileUploadService.getTotalFileSizeByUser(userId);
        Long fileCount = fileUploadService.getFileCountByUser(userId);
        
        return ResponseEntity.ok(Map.of(
            "totalFileSize", totalSize,
            "fileCount", fileCount
        ));
    }
    
    // Get file count by upload type
    @GetMapping("/stats/type/{uploadType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<Object> getFileCountByUploadType(@PathVariable FileUpload.UploadType uploadType) {
        Long count = fileUploadService.getFileCountByUploadType(uploadType);
        
        return ResponseEntity.ok(Map.of("fileCount", count));
    }
    
    // Get all upload types
    @GetMapping("/upload-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER', 'USER')")
    public ResponseEntity<List<FileUpload.UploadType>> getAllUploadTypes() {
        List<FileUpload.UploadType> uploadTypes = fileUploadService.getAllUploadTypes();
        return ResponseEntity.ok(uploadTypes);
    }
    
    // Get all content types
    @GetMapping("/content-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<List<String>> getAllContentTypes() {
        List<String> contentTypes = fileUploadService.getAllContentTypes();
        return ResponseEntity.ok(contentTypes);
    }
    
    // Get all file extensions
    @GetMapping("/extensions")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<List<String>> getAllFileExtensions() {
        List<String> extensions = fileUploadService.getAllFileExtensions();
        return ResponseEntity.ok(extensions);
    }
} 