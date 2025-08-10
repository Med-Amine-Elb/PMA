package com.telephonemanager.controller;

import com.telephonemanager.dto.SystemSettingsDto;
import com.telephonemanager.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/settings/system")
@CrossOrigin(origins = "*")
public class SystemSettingsController {
    
    @Autowired
    private SystemSettingsService systemSettingsService;
    
    // Get all system settings with pagination
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getAllSettings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "key") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SystemSettingsDto> settings = systemSettingsService.getAllSettings(pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get setting by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingsDto> getSettingById(@PathVariable Long id) {
        Optional<SystemSettingsDto> setting = systemSettingsService.getSettingById(id);
        return setting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get setting by key
    @GetMapping("/key/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingsDto> getSettingByKey(@PathVariable String key) {
        Optional<SystemSettingsDto> setting = systemSettingsService.getSettingByKey(key);
        return setting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get setting value by key
    @GetMapping("/value/{key}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSIGNER')")
    public ResponseEntity<String> getSettingValue(@PathVariable String key) {
        String value = systemSettingsService.getSettingValue(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }
    
    // Create new setting
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingsDto> createSetting(@RequestBody SystemSettingsDto settingsDto) {
        SystemSettingsDto createdSetting = systemSettingsService.createSetting(settingsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSetting);
    }
    
    // Update setting
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingsDto> updateSetting(
            @PathVariable Long id, 
            @RequestBody SystemSettingsDto settingsDto) {
        Optional<SystemSettingsDto> updatedSetting = systemSettingsService.updateSetting(id, settingsDto);
        return updatedSetting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update setting by key
    @PutMapping("/key/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingsDto> updateSettingByKey(
            @PathVariable String key, 
            @RequestParam String value) {
        Optional<SystemSettingsDto> updatedSetting = systemSettingsService.updateSettingByKey(key, value);
        return updatedSetting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete setting
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        boolean deleted = systemSettingsService.deleteSetting(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Delete setting by key
    @DeleteMapping("/key/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSettingByKey(@PathVariable String key) {
        boolean deleted = systemSettingsService.deleteSettingByKey(key);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Get settings by category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getSettingsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.getSettingsByCategory(category, pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get settings by category as list
    @GetMapping("/category/{category}/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemSettingsDto>> getSettingsByCategoryList(@PathVariable String category) {
        List<SystemSettingsDto> settings = systemSettingsService.getSettingsByCategory(category);
        return ResponseEntity.ok(settings);
    }
    
    // Get settings by data type
    @GetMapping("/data-type/{dataType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getSettingsByDataType(
            @PathVariable String dataType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.getSettingsByDataType(dataType, pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get editable settings
    @GetMapping("/editable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getEditableSettings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.getEditableSettings(pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get encrypted settings
    @GetMapping("/encrypted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getEncryptedSettings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.getEncryptedSettings(pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Search settings by key
    @GetMapping("/search/key")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> searchSettingsByKey(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.searchSettingsByKey(key, pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Search settings by description
    @GetMapping("/search/description")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> searchSettingsByDescription(
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.searchSettingsByDescription(description, pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get settings by category and editable
    @GetMapping("/category/{category}/editable/{isEditable}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemSettingsDto>> getSettingsByCategoryAndEditable(
            @PathVariable String category,
            @PathVariable boolean isEditable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemSettingsDto> settings = systemSettingsService.getSettingsByCategoryAndEditable(category, isEditable, pageable);
        return ResponseEntity.ok(settings);
    }
    
    // Get all categories
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = systemSettingsService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    // Check if setting exists
    @GetMapping("/exists/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> settingExists(@PathVariable String key) {
        boolean exists = systemSettingsService.settingExists(key);
        return ResponseEntity.ok(exists);
    }
} 