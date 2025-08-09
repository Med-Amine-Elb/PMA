package com.telephonemanager.service;

import com.telephonemanager.dto.SystemSettingsDto;
import com.telephonemanager.entity.SystemSettings;
import com.telephonemanager.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SystemSettingsService {
    
    @Autowired
    private SystemSettingsRepository systemSettingsRepository;
    
    public Page<SystemSettingsDto> getAllSettings(Pageable pageable) {
        return systemSettingsRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    public Optional<SystemSettingsDto> getSettingById(Long id) {
        return systemSettingsRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<SystemSettingsDto> getSettingByKey(String key) {
        return systemSettingsRepository.findByKey(key)
                .map(this::convertToDto);
    }
    
    public SystemSettingsDto createSetting(SystemSettingsDto settingsDto) {
        SystemSettings setting = convertToEntity(settingsDto);
        SystemSettings savedSetting = systemSettingsRepository.save(setting);
        return convertToDto(savedSetting);
    }
    
    public Optional<SystemSettingsDto> updateSetting(Long id, SystemSettingsDto settingsDto) {
        return systemSettingsRepository.findById(id)
                .map(existingSetting -> {
                    updateSettingFromDto(existingSetting, settingsDto);
                    SystemSettings savedSetting = systemSettingsRepository.save(existingSetting);
                    return convertToDto(savedSetting);
                });
    }
    
    public Optional<SystemSettingsDto> updateSettingByKey(String key, String value) {
        return systemSettingsRepository.findByKey(key)
                .map(setting -> {
                    setting.setValue(value);
                    SystemSettings savedSetting = systemSettingsRepository.save(setting);
                    return convertToDto(savedSetting);
                });
    }
    
    public boolean deleteSetting(Long id) {
        if (systemSettingsRepository.existsById(id)) {
            systemSettingsRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean deleteSettingByKey(String key) {
        Optional<SystemSettings> setting = systemSettingsRepository.findByKey(key);
        if (setting.isPresent()) {
            systemSettingsRepository.delete(setting.get());
            return true;
        }
        return false;
    }
    
    public Page<SystemSettingsDto> getSettingsByCategory(String category, Pageable pageable) {
        return systemSettingsRepository.findByCategory(category, pageable)
                .map(this::convertToDto);
    }
    
    public List<SystemSettingsDto> getSettingsByCategory(String category) {
        return systemSettingsRepository.findByCategory(category)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    public Page<SystemSettingsDto> getSettingsByDataType(String dataType, Pageable pageable) {
        return systemSettingsRepository.findByDataType(dataType, pageable)
                .map(this::convertToDto);
    }
    
    public Page<SystemSettingsDto> getEditableSettings(Pageable pageable) {
        return systemSettingsRepository.findByIsEditableTrue(pageable)
                .map(this::convertToDto);
    }
    
    public Page<SystemSettingsDto> getEncryptedSettings(Pageable pageable) {
        return systemSettingsRepository.findByIsEncryptedTrue(pageable)
                .map(this::convertToDto);
    }
    
    public Page<SystemSettingsDto> searchSettingsByKey(String key, Pageable pageable) {
        return systemSettingsRepository.findByKeyContainingIgnoreCase(key, pageable)
                .map(this::convertToDto);
    }
    
    public Page<SystemSettingsDto> searchSettingsByDescription(String description, Pageable pageable) {
        return systemSettingsRepository.findByDescriptionContainingIgnoreCase(description, pageable)
                .map(this::convertToDto);
    }
    
    public Page<SystemSettingsDto> getSettingsByCategoryAndEditable(String category, boolean isEditable, Pageable pageable) {
        return systemSettingsRepository.findByCategoryAndEditable(category, isEditable, pageable)
                .map(this::convertToDto);
    }
    
    public List<String> getAllCategories() {
        return systemSettingsRepository.findAllCategories();
    }
    
    public boolean settingExists(String key) {
        return systemSettingsRepository.existsByKey(key);
    }
    
    public String getSettingValue(String key) {
        return systemSettingsRepository.findByKey(key)
                .map(SystemSettings::getValue)
                .orElse(null);
    }
    
    public String getSettingValue(String key, String defaultValue) {
        return systemSettingsRepository.findByKey(key)
                .map(SystemSettings::getValue)
                .orElse(defaultValue);
    }
    
    public boolean getBooleanSettingValue(String key, boolean defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    public Integer getIntegerSettingValue(String key, Integer defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public Double getDoubleSettingValue(String key, Double defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private SystemSettingsDto convertToDto(SystemSettings setting) {
        SystemSettingsDto dto = new SystemSettingsDto();
        dto.setId(setting.getId());
        dto.setKey(setting.getKey());
        dto.setValue(setting.getValue());
        dto.setDescription(setting.getDescription());
        dto.setCategory(setting.getCategory());
        dto.setDataType(setting.getDataType());
        dto.setEncrypted(setting.isEncrypted());
        dto.setEditable(setting.isEditable());
        dto.setCreatedAt(setting.getCreatedAt());
        dto.setUpdatedAt(setting.getUpdatedAt());
        return dto;
    }
    
    private SystemSettings convertToEntity(SystemSettingsDto dto) {
        SystemSettings setting = new SystemSettings();
        setting.setKey(dto.getKey());
        setting.setValue(dto.getValue());
        setting.setDescription(dto.getDescription());
        setting.setCategory(dto.getCategory());
        setting.setDataType(dto.getDataType());
        setting.setEncrypted(dto.isEncrypted());
        setting.setEditable(dto.isEditable());
        return setting;
    }
    
    private void updateSettingFromDto(SystemSettings setting, SystemSettingsDto dto) {
        if (dto.getKey() != null) setting.setKey(dto.getKey());
        if (dto.getValue() != null) setting.setValue(dto.getValue());
        if (dto.getDescription() != null) setting.setDescription(dto.getDescription());
        if (dto.getCategory() != null) setting.setCategory(dto.getCategory());
        if (dto.getDataType() != null) setting.setDataType(dto.getDataType());
        setting.setEncrypted(dto.isEncrypted());
        setting.setEditable(dto.isEditable());
    }
} 