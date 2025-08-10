package com.telephonemanager.repository;

import com.telephonemanager.entity.FileUpload;
import com.telephonemanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    
    // Find files by upload type
    Page<FileUpload> findByUploadType(FileUpload.UploadType uploadType, Pageable pageable);
    
    // Find files by uploaded by user
    Page<FileUpload> findByUploadedBy(User uploadedBy, Pageable pageable);
    
    // Find files by uploaded by user ID
    @Query("SELECT fu FROM FileUpload fu WHERE fu.uploadedBy.id = :userId")
    Page<FileUpload> findByUploadedById(@Param("userId") Long userId, Pageable pageable);
    
    // Find files by related entity
    Page<FileUpload> findByRelatedEntityTypeAndRelatedEntityId(String relatedEntityType, Long relatedEntityId, Pageable pageable);
    
    // Find public files
    Page<FileUpload> findByIsPublicTrue(Pageable pageable);
    
    // Find files by content type
    Page<FileUpload> findByContentType(String contentType, Pageable pageable);
    
    // Find files by file extension
    Page<FileUpload> findByFileExtension(String fileExtension, Pageable pageable);
    
    // Find files by file name containing
    Page<FileUpload> findByFileNameContainingIgnoreCase(String fileName, Pageable pageable);
    
    // Find files by original name containing
    Page<FileUpload> findByOriginalNameContainingIgnoreCase(String originalName, Pageable pageable);
    
    // Find files by description containing
    Page<FileUpload> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    // Find files by file size range
    @Query("SELECT fu FROM FileUpload fu WHERE fu.fileSize BETWEEN :minSize AND :maxSize")
    Page<FileUpload> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize, Pageable pageable);
    
    // Find files larger than size
    @Query("SELECT fu FROM FileUpload fu WHERE fu.fileSize > :size")
    Page<FileUpload> findByFileSizeGreaterThan(@Param("size") Long size, Pageable pageable);
    
    // Find files smaller than size
    @Query("SELECT fu FROM FileUpload fu WHERE fu.fileSize < :size")
    Page<FileUpload> findByFileSizeLessThan(@Param("size") Long size, Pageable pageable);
    
    // Find files by upload type and user
    Page<FileUpload> findByUploadTypeAndUploadedBy(FileUpload.UploadType uploadType, User uploadedBy, Pageable pageable);
    
    // Find files by upload type and public
    Page<FileUpload> findByUploadTypeAndIsPublic(FileUpload.UploadType uploadType, boolean isPublic, Pageable pageable);
    
    // Get total file size by user
    @Query("SELECT SUM(fu.fileSize) FROM FileUpload fu WHERE fu.uploadedBy.id = :userId")
    Long getTotalFileSizeByUser(@Param("userId") Long userId);
    
    // Get file count by user
    @Query("SELECT COUNT(fu) FROM FileUpload fu WHERE fu.uploadedBy.id = :userId")
    Long getFileCountByUser(@Param("userId") Long userId);
    
    // Get file count by upload type
    @Query("SELECT COUNT(fu) FROM FileUpload fu WHERE fu.uploadType = :uploadType")
    Long getFileCountByUploadType(@Param("uploadType") FileUpload.UploadType uploadType);
    
    // Get all upload types
    @Query("SELECT DISTINCT fu.uploadType FROM FileUpload fu ORDER BY fu.uploadType")
    List<FileUpload.UploadType> findAllUploadTypes();
    
    // Get all content types
    @Query("SELECT DISTINCT fu.contentType FROM FileUpload fu ORDER BY fu.contentType")
    List<String> findAllContentTypes();
    
    // Get all file extensions
    @Query("SELECT DISTINCT fu.fileExtension FROM FileUpload fu WHERE fu.fileExtension IS NOT NULL ORDER BY fu.fileExtension")
    List<String> findAllFileExtensions();
    
    // Find files by multiple upload types
    @Query("SELECT fu FROM FileUpload fu WHERE fu.uploadType IN :uploadTypes")
    Page<FileUpload> findByUploadTypeIn(@Param("uploadTypes") List<FileUpload.UploadType> uploadTypes, Pageable pageable);
    
    // Find files by multiple content types
    @Query("SELECT fu FROM FileUpload fu WHERE fu.contentType IN :contentTypes")
    Page<FileUpload> findByContentTypeIn(@Param("contentTypes") List<String> contentTypes, Pageable pageable);
} 