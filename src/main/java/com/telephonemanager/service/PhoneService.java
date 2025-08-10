package com.telephonemanager.service;

import com.telephonemanager.dto.PhoneDto;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.PhoneRepository;
import com.telephonemanager.repository.UserRepository;
import com.telephonemanager.service.AssignmentHistoryService;
import com.telephonemanager.entity.AssignmentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignmentHistoryService assignmentHistoryService;

    public Page<PhoneDto> getPhones(int page, int limit, Phone.Status status, String brand, String model) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Phone> phones = phoneRepository.findPhonesWithFilters(status, brand, model, pageable);
        return phones.map(PhoneDto::new);
    }

    public Optional<PhoneDto> getPhoneById(Long id) {
        return phoneRepository.findById(id).map(PhoneDto::new);
    }

    public PhoneDto createPhone(PhoneDto dto) {
        if (phoneRepository.findByImei(dto.getImei()).isPresent()) {
            throw new RuntimeException("IMEI already exists");
        }
        Phone phone = new Phone();
        phone.setBrand(dto.getBrand());
        phone.setModel(dto.getModel());
        phone.setImei(dto.getImei());
        phone.setStatus(dto.getStatus());
        phone.setNotes(dto.getNotes());
        // Add missing fields
        phone.setSerialNumber(dto.getSerialNumber());
        phone.setStorage(dto.getStorage());
        phone.setColor(dto.getColor());
        phone.setPrice(dto.getPrice());
        phone.setCondition(dto.getCondition());
        phone.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getAssignedToId() != null) {
            User user = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            phone.setAssignedTo(user);
            phone.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
            assignmentHistoryService.record(
                AssignmentHistory.Type.PHONE,
                null, // phone not saved yet, will record in update
                null,
                user.getId(),
                AssignmentHistory.Action.ASSIGN,
                "Phone assigned on creation"
            );
        }
        Phone saved = phoneRepository.save(phone);
        // If assigned, update the itemId in history
        if (dto.getAssignedToId() != null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.PHONE,
                saved.getId(),
                null,
                saved.getAssignedTo().getId(),
                AssignmentHistory.Action.ASSIGN,
                "Phone assigned on creation"
            );
        }
        return new PhoneDto(saved);
    }

    public PhoneDto updatePhone(Long id, PhoneDto dto) {
        Phone phone = phoneRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Phone not found"));
        Long oldUserId = phone.getAssignedTo() != null ? phone.getAssignedTo().getId() : null;
        Long newUserId = dto.getAssignedToId();
        if (!phone.getImei().equals(dto.getImei()) && phoneRepository.findByImei(dto.getImei()).isPresent()) {
            throw new RuntimeException("IMEI already exists");
        }
        phone.setBrand(dto.getBrand());
        phone.setModel(dto.getModel());
        phone.setImei(dto.getImei());
        phone.setStatus(dto.getStatus());
        phone.setNotes(dto.getNotes());
        // Add missing fields
        phone.setSerialNumber(dto.getSerialNumber());
        phone.setStorage(dto.getStorage());
        phone.setColor(dto.getColor());
        phone.setPrice(dto.getPrice());
        phone.setCondition(dto.getCondition());
        phone.setPurchaseDate(dto.getPurchaseDate());
        if (newUserId != null) {
            User user = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            phone.setAssignedTo(user);
            phone.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
        } else {
            phone.setAssignedTo(null);
            phone.setAssignedDate(null);
        }
        Phone updated = phoneRepository.save(phone);
        // Record assignment history
        if (oldUserId == null && newUserId != null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.PHONE,
                updated.getId(),
                null,
                newUserId,
                AssignmentHistory.Action.ASSIGN,
                "Phone assigned"
            );
        } else if (oldUserId != null && newUserId == null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.PHONE,
                updated.getId(),
                oldUserId,
                null,
                AssignmentHistory.Action.UNASSIGN,
                "Phone unassigned"
            );
        } else if (oldUserId != null && newUserId != null && !oldUserId.equals(newUserId)) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.PHONE,
                updated.getId(),
                oldUserId,
                newUserId,
                AssignmentHistory.Action.TRANSFER,
                "Phone transferred"
            );
        }
        return new PhoneDto(updated);
    }

    public void deletePhone(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new RuntimeException("Phone not found");
        }
        phoneRepository.deleteById(id);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    public PhoneDto assignPhone(Long phoneId, Long userId) {
        Phone phone = phoneRepository.findById(phoneId)
            .orElseThrow(() -> new RuntimeException("Phone not found"));
        
        if (phone.getAssignedTo() != null) {
            throw new RuntimeException("Phone is already assigned to a user");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        phone.setAssignedTo(user);
        phone.setAssignedDate(LocalDate.now());
        Phone saved = phoneRepository.save(phone);
        
        // Record assignment history
        assignmentHistoryService.record(
            AssignmentHistory.Type.PHONE,
            saved.getId(),
            null,
            user.getId(),
            AssignmentHistory.Action.ASSIGN,
            "Phone assigned via explicit endpoint"
        );
        
        return new PhoneDto(saved);
    }

    public PhoneDto unassignPhone(Long phoneId) {
        Phone phone = phoneRepository.findById(phoneId)
            .orElseThrow(() -> new RuntimeException("Phone not found"));
        
        if (phone.getAssignedTo() == null) {
            throw new RuntimeException("Phone is not assigned to any user");
        }
        
        Long oldUserId = phone.getAssignedTo().getId();
        phone.setAssignedTo(null);
        phone.setAssignedDate(null);
        Phone saved = phoneRepository.save(phone);
        
        // Record unassignment history
        assignmentHistoryService.record(
            AssignmentHistory.Type.PHONE,
            saved.getId(),
            oldUserId,
            null,
            AssignmentHistory.Action.UNASSIGN,
            "Phone unassigned via explicit endpoint"
        );
        
        return new PhoneDto(saved);
    }

    public PhoneDto transferPhone(Long phoneId, Long newUserId) {
        Phone phone = phoneRepository.findById(phoneId)
            .orElseThrow(() -> new RuntimeException("Phone not found"));
        
        if (phone.getAssignedTo() == null) {
            throw new RuntimeException("Phone is not assigned to any user");
        }
        
        User newUser = userRepository.findById(newUserId)
            .orElseThrow(() -> new RuntimeException("New user not found"));
        
        Long oldUserId = phone.getAssignedTo().getId();
        phone.setAssignedTo(newUser);
        phone.setAssignedDate(LocalDate.now());
        Phone saved = phoneRepository.save(phone);
        
        // Record transfer history
        assignmentHistoryService.record(
            AssignmentHistory.Type.PHONE,
            saved.getId(),
            oldUserId,
            newUser.getId(),
            AssignmentHistory.Action.TRANSFER,
            "Phone transferred via explicit endpoint"
        );
        
        return new PhoneDto(saved);
    }
} 