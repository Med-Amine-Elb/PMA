package com.telephonemanager.service;

import com.telephonemanager.dto.AttributionDto;
import com.telephonemanager.entity.Attribution;
import com.telephonemanager.entity.Attribution.Status;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.SimCard;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.AttributionRepository;
import com.telephonemanager.repository.PhoneRepository;
import com.telephonemanager.repository.SimCardRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttributionService {
    
    @Autowired
    private AttributionRepository attributionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PhoneRepository phoneRepository;
    
    @Autowired
    private SimCardRepository simCardRepository;
    
    @Autowired
    private AssignmentHistoryService assignmentHistoryService;

    public Page<AttributionDto> getAttributions(int page, int limit, Status status, Long userId, Long assignedById, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Attribution> attributions = attributionRepository.findWithFilters(userId, status, assignedById, search, pageable);
        return attributions.map(AttributionDto::new);
    }

    public Optional<AttributionDto> getAttributionById(Long id) {
        return attributionRepository.findById(id).map(AttributionDto::new);
    }

    public AttributionDto createAttribution(AttributionDto dto, Long assignedById) {
        // Validate user
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate assigned by user
        User assignedBy = userRepository.findById(assignedById)
            .orElseThrow(() -> new RuntimeException("Assigned by user not found"));
        
        // Validate phone if provided
        Phone phone = null;
        if (dto.getPhoneId() != null) {
            phone = phoneRepository.findById(dto.getPhoneId())
                .orElseThrow(() -> new RuntimeException("Phone not found"));
            
            // Check if phone is available
            if (phone.getStatus() != Phone.Status.AVAILABLE) {
                throw new RuntimeException("Phone is not available for assignment");
            }
            
            // Check if phone is already assigned
            if (attributionRepository.countByPhoneIdAndStatus(dto.getPhoneId(), Status.ACTIVE) > 0) {
                throw new RuntimeException("Phone is already assigned to another user");
            }
        }
        
        // Validate SIM card if provided
        SimCard simCard = null;
        if (dto.getSimCardId() != null) {
            simCard = simCardRepository.findById(dto.getSimCardId())
                .orElseThrow(() -> new RuntimeException("SIM card not found"));
            
            // Check if SIM card is available
            if (simCard.getStatus() != SimCard.Status.AVAILABLE) {
                throw new RuntimeException("SIM card is not available for assignment");
            }
            
            // Check if SIM card is already assigned
            if (attributionRepository.countBySimCardIdAndStatus(dto.getSimCardId(), Status.ACTIVE) > 0) {
                throw new RuntimeException("SIM card is already assigned to another user");
            }
        }
        
        // Create attribution
        Attribution attribution = new Attribution();
        attribution.setUser(user);
        attribution.setPhone(phone);
        attribution.setSimCard(simCard);
        attribution.setAssignedBy(assignedBy);
        attribution.setAssignmentDate(dto.getAssignmentDate() != null ? dto.getAssignmentDate() : LocalDate.now());
        attribution.setStatus(Status.ACTIVE);
        attribution.setNotes(dto.getNotes());
        
        Attribution saved = attributionRepository.save(attribution);
        
        // Update phone and SIM card status if assigned
        if (phone != null) {
            phone.setStatus(Phone.Status.ASSIGNED);
            phone.setAssignedTo(user);
            phone.setAssignedDate(attribution.getAssignmentDate());
            phoneRepository.save(phone);
            
            // Record assignment history
            assignmentHistoryService.record(
                com.telephonemanager.entity.AssignmentHistory.Type.PHONE,
                phone.getId(),
                null,
                user.getId(),
                com.telephonemanager.entity.AssignmentHistory.Action.ASSIGN,
                "Phone assigned via attribution"
            );
        }
        
        if (simCard != null) {
            simCard.setStatus(SimCard.Status.ASSIGNED);
            simCard.setAssignedTo(user);
            simCard.setAssignedDate(attribution.getAssignmentDate());
            simCardRepository.save(simCard);
            
            // Record assignment history
            assignmentHistoryService.record(
                com.telephonemanager.entity.AssignmentHistory.Type.SIM,
                simCard.getId(),
                null,
                user.getId(),
                com.telephonemanager.entity.AssignmentHistory.Action.ASSIGN,
                "SIM card assigned via attribution"
            );
        }
        
        return new AttributionDto(saved);
    }

    public AttributionDto updateAttribution(Long id, AttributionDto dto) {
        System.out.println("=== ATTRIBUTION SERVICE: Update attribution called");
        System.out.println("=== ATTRIBUTION SERVICE: ID: " + id);
        System.out.println("=== ATTRIBUTION SERVICE: DTO status: " + dto.getStatus());
        System.out.println("=== ATTRIBUTION SERVICE: DTO notes: " + dto.getNotes());
        
        Attribution attribution = attributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attribution not found"));
        
        System.out.println("=== ATTRIBUTION SERVICE: Current status: " + attribution.getStatus());
        
        // Update notes and status (other fields should not be modified after creation)
        attribution.setNotes(dto.getNotes());
        if (dto.getStatus() != null) {
            attribution.setStatus(dto.getStatus());
            System.out.println("=== ATTRIBUTION SERVICE: Status updated to: " + dto.getStatus());
        }
        
        Attribution saved = attributionRepository.save(attribution);
        System.out.println("=== ATTRIBUTION SERVICE: Saved status: " + saved.getStatus());
        return new AttributionDto(saved);
    }

    public void deleteAttribution(Long id) {
        Attribution attribution = attributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attribution not found"));
        
        // Return equipment before deleting
        if (attribution.getStatus() == Status.ACTIVE) {
            returnAttribution(id, "Attribution deleted");
        }
        
        attributionRepository.deleteById(id);
    }

    public AttributionDto returnAttribution(Long id, String notes) {
        Attribution attribution = attributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attribution not found"));
        
        if (attribution.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Attribution is not active and cannot be returned");
        }
        
        attribution.setStatus(Status.RETURNED);
        attribution.setReturnDate(LocalDate.now());
        attribution.setNotes(notes != null ? notes : attribution.getNotes());
        
        Attribution saved = attributionRepository.save(attribution);
        
        // Update phone status if present
        if (attribution.getPhone() != null) {
            Phone phone = attribution.getPhone();
            phone.setStatus(Phone.Status.AVAILABLE);
            phone.setAssignedTo(null);
            phone.setAssignedDate(null);
            phoneRepository.save(phone);
            
            // Record unassignment history
            assignmentHistoryService.record(
                com.telephonemanager.entity.AssignmentHistory.Type.PHONE,
                phone.getId(),
                attribution.getUser().getId(),
                null,
                com.telephonemanager.entity.AssignmentHistory.Action.UNASSIGN,
                "Phone returned via attribution"
            );
        }
        
        // Update SIM card status if present
        if (attribution.getSimCard() != null) {
            SimCard simCard = attribution.getSimCard();
            simCard.setStatus(SimCard.Status.AVAILABLE);
            simCard.setAssignedTo(null);
            simCard.setAssignedDate(null);
            simCardRepository.save(simCard);
            
            // Record unassignment history
            assignmentHistoryService.record(
                com.telephonemanager.entity.AssignmentHistory.Type.SIM,
                simCard.getId(),
                attribution.getUser().getId(),
                null,
                com.telephonemanager.entity.AssignmentHistory.Action.UNASSIGN,
                "SIM card returned via attribution"
            );
        }
        
        return new AttributionDto(saved);
    }

    public List<AttributionDto> getAttributionHistoryBySimCard(Long simCardId) {
        List<Attribution> history = attributionRepository.findHistoryBySimCardId(simCardId);
        return history.stream().map(AttributionDto::new).collect(Collectors.toList());
    }

    public List<AttributionDto> getAttributionHistoryByPhone(Long phoneId) {
        List<Attribution> history = attributionRepository.findHistoryByPhoneId(phoneId);
        return history.stream().map(AttributionDto::new).collect(Collectors.toList());
    }

    public List<AttributionDto> getActiveAttributionsByUser(Long userId) {
        List<Attribution> attributions = attributionRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
        return attributions.stream().map(AttributionDto::new).collect(Collectors.toList());
    }
} 