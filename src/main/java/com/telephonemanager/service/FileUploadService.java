package com.telephonemanager.service;

import com.telephonemanager.dto.FileUploadDto;
import com.telephonemanager.entity.FileUpload;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.FileUploadRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FileUploadService {
    
    @Autowired
    private FileUploadRepository fileUploadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${app.file.max-size:10485760}") // 10MB default
    private long maxFileSize;
    
    @Value("${app.file.allowed-types:image/jpeg,image/png,image/gif,application/pdf,text/plain,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document}")
    private String allowedTypes;
    
    public FileUploadDto uploadFile(MultipartFile file, Long uploadedById, FileUpload.UploadType uploadType,
                                   String description, String relatedEntityType, Long relatedEntityId, boolean isPublic) {
        
        // Validate file
        validateFile(file);
        
        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = generateUniqueFileName(fileExtension);
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + uniqueFileName, e);
        }
        
        // Get user
        Optional<User> user = userRepository.findById(uploadedById);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        // Create file upload entity
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(uniqueFileName);
        fileUpload.setOriginalName(originalFilename);
        fileUpload.setFilePath(filePath.toString());
        fileUpload.setFileSize(file.getSize());
        fileUpload.setContentType(file.getContentType());
        fileUpload.setFileExtension(fileExtension);
        fileUpload.setUploadType(uploadType);
        fileUpload.setUploadedBy(user.get());
        fileUpload.setDescription(description);
        fileUpload.setRelatedEntityType(relatedEntityType);
        fileUpload.setRelatedEntityId(relatedEntityId);
        fileUpload.setPublic(isPublic);
        
        // Save to database
        FileUpload savedFileUpload = fileUploadRepository.save(fileUpload);
        
        return convertToDto(savedFileUpload);
    }
    
    public Resource downloadFile(Long fileId) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(fileId);
        if (fileUpload.isEmpty()) {
            throw new RuntimeException("File not found");
        }
        
        FileUpload file = fileUpload.get();
        Path filePath = Paths.get(file.getFilePath());
        
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // Increment download count
                file.setDownloadCount(file.getDownloadCount() + 1);
                fileUploadRepository.save(file);
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }
    
    public FileUploadDto getFileById(Long id) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(id);
        return fileUpload.map(this::convertToDto).orElse(null);
    }
    
    public Page<FileUploadDto> getAllFiles(Pageable pageable) {
        return fileUploadRepository.findAll(pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesByUploadType(FileUpload.UploadType uploadType, Pageable pageable) {
        return fileUploadRepository.findByUploadType(uploadType, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesByUser(Long userId, Pageable pageable) {
        return fileUploadRepository.findByUploadedById(userId, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesByRelatedEntity(String relatedEntityType, Long relatedEntityId, Pageable pageable) {
        return fileUploadRepository.findByRelatedEntityTypeAndRelatedEntityId(relatedEntityType, relatedEntityId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getPublicFiles(Pageable pageable) {
        return fileUploadRepository.findByIsPublicTrue(pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> searchFilesByFileName(String fileName, Pageable pageable) {
        return fileUploadRepository.findByFileNameContainingIgnoreCase(fileName, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> searchFilesByOriginalName(String originalName, Pageable pageable) {
        return fileUploadRepository.findByOriginalNameContainingIgnoreCase(originalName, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> searchFilesByDescription(String description, Pageable pageable) {
        return fileUploadRepository.findByDescriptionContainingIgnoreCase(description, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesByContentType(String contentType, Pageable pageable) {
        return fileUploadRepository.findByContentType(contentType, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesByFileExtension(String fileExtension, Pageable pageable) {
        return fileUploadRepository.findByFileExtension(fileExtension, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesBySizeRange(Long minSize, Long maxSize, Pageable pageable) {
        return fileUploadRepository.findByFileSizeBetween(minSize, maxSize, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesLargerThan(Long size, Pageable pageable) {
        return fileUploadRepository.findByFileSizeGreaterThan(size, pageable).map(this::convertToDto);
    }
    
    public Page<FileUploadDto> getFilesSmallerThan(Long size, Pageable pageable) {
        return fileUploadRepository.findByFileSizeLessThan(size, pageable).map(this::convertToDto);
    }
    
    public boolean deleteFile(Long id) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(id);
        if (fileUpload.isPresent()) {
            FileUpload file = fileUpload.get();
            
            // Delete physical file
            try {
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but continue with database deletion
                System.err.println("Could not delete physical file: " + e.getMessage());
            }
            
            // Delete from database
            fileUploadRepository.delete(file);
            return true;
        }
        return false;
    }
    
    public FileUploadDto updateFileMetadata(Long id, String description, boolean isPublic) {
        Optional<FileUpload> fileUpload = fileUploadRepository.findById(id);
        if (fileUpload.isPresent()) {
            FileUpload file = fileUpload.get();
            file.setDescription(description);
            file.setPublic(isPublic);
            FileUpload savedFile = fileUploadRepository.save(file);
            return convertToDto(savedFile);
        }
        return null;
    }
    
    public Long getTotalFileSizeByUser(Long userId) {
        Long totalSize = fileUploadRepository.getTotalFileSizeByUser(userId);
        return totalSize != null ? totalSize : 0L;
    }
    
    public Long getFileCountByUser(Long userId) {
        Long count = fileUploadRepository.getFileCountByUser(userId);
        return count != null ? count : 0L;
    }
    
    public Long getFileCountByUploadType(FileUpload.UploadType uploadType) {
        Long count = fileUploadRepository.getFileCountByUploadType(uploadType);
        return count != null ? count : 0L;
    }
    
    public List<FileUpload.UploadType> getAllUploadTypes() {
        return fileUploadRepository.findAllUploadTypes();
    }
    
    public List<String> getAllContentTypes() {
        return fileUploadRepository.findAllContentTypes();
    }
    
    public List<String> getAllFileExtensions() {
        return fileUploadRepository.findAllFileExtensions();
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }
        
        String contentType = file.getContentType();
        if (contentType != null && !allowedTypes.contains(contentType)) {
            throw new RuntimeException("File type not allowed");
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    private String generateUniqueFileName(String extension) {
        String uuid = UUID.randomUUID().toString();
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }
    
    private FileUploadDto convertToDto(FileUpload fileUpload) {
        FileUploadDto dto = new FileUploadDto();
        dto.setId(fileUpload.getId());
        dto.setFileName(fileUpload.getFileName());
        dto.setOriginalName(fileUpload.getOriginalName());
        dto.setFilePath(fileUpload.getFilePath());
        dto.setFileSize(fileUpload.getFileSize());
        dto.setContentType(fileUpload.getContentType());
        dto.setFileExtension(fileUpload.getFileExtension());
        dto.setUploadType(fileUpload.getUploadType().name());
        dto.setUploadedById(fileUpload.getUploadedBy().getId());
        dto.setUploadedByName(fileUpload.getUploadedBy().getName());
        dto.setRelatedEntityType(fileUpload.getRelatedEntityType());
        dto.setRelatedEntityId(fileUpload.getRelatedEntityId());
        dto.setDescription(fileUpload.getDescription());
        dto.setPublic(fileUpload.isPublic());
        dto.setDownloadCount(fileUpload.getDownloadCount());
        dto.setUploadedAt(fileUpload.getUploadedAt());
        dto.setUpdatedAt(fileUpload.getUpdatedAt());
        dto.setDownloadUrl("/api/files/" + fileUpload.getId() + "/download");
        return dto;
    }
} 