package com.telephonemanager.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.telephonemanager.dto.SimCardDto;
import com.telephonemanager.entity.AssignmentHistory;
import com.telephonemanager.entity.SimCard;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.SimCardRepository;
import com.telephonemanager.repository.UserRepository;

@Service
public class SimCardService {
    @Autowired
    private SimCardRepository simCardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignmentHistoryService assignmentHistoryService;

    public Page<SimCardDto> getSimCards(int page, int limit, SimCard.Status status, String number, String iccid) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SimCard> sims = simCardRepository.findSimCardsWithFilters(status, number, iccid, pageable);
        return sims.map(SimCardDto::new);
    }

    public Optional<SimCardDto> getSimCardById(Long id) {
        return simCardRepository.findById(id).map(SimCardDto::new);
    }

    public SimCardDto createSimCard(SimCardDto dto) {
        if (simCardRepository.findByIccid(dto.getIccid()).isPresent()) {
            throw new RuntimeException("ICCID already exists");
        }
        if (simCardRepository.findByPuk(dto.getPuk()).isPresent()) {
            throw new RuntimeException("PUK already exists");
        }
        SimCard sim = new SimCard();
        sim.setNumber(dto.getNumber());
        sim.setIccid(dto.getIccid());
        sim.setStatus(dto.getStatus());
        sim.setNotes(dto.getNotes());
        sim.setPin(dto.getPin());
        sim.setPuk(dto.getPuk());
        sim.setPoke(dto.getPoke());
        sim.setCarrier(dto.getCarrier());
        sim.setPlan(dto.getPlan());
        sim.setMonthlyFee(dto.getMonthlyFee());
        sim.setDataLimit(dto.getDataLimit());
        sim.setActivationDate(dto.getActivationDate());
        sim.setExpiryDate(dto.getExpiryDate());
        if (dto.getAssignedToId() != null) {
            User user = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            sim.setAssignedTo(user);
            sim.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
            assignmentHistoryService.record(
                AssignmentHistory.Type.SIM,
                null, // sim not saved yet, will record in update
                null,
                user.getId(),
                AssignmentHistory.Action.ASSIGN,
                "SIM assigned on creation"
            );
        }
        SimCard saved = simCardRepository.save(sim);
        // If assigned, update the itemId in history
        if (dto.getAssignedToId() != null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.SIM,
                saved.getId(),
                null,
                saved.getAssignedTo().getId(),
                AssignmentHistory.Action.ASSIGN,
                "SIM assigned on creation"
            );
        }
        return new SimCardDto(saved);
    }

    public SimCardDto updateSimCard(Long id, SimCardDto dto) {
        SimCard sim = simCardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("SimCard not found"));
        Long oldUserId = sim.getAssignedTo() != null ? sim.getAssignedTo().getId() : null;
        Long newUserId = dto.getAssignedToId();
        if (!sim.getIccid().equals(dto.getIccid()) && simCardRepository.findByIccid(dto.getIccid()).isPresent()) {
            throw new RuntimeException("ICCID already exists");
        }
        if (!sim.getPuk().equals(dto.getPuk()) && simCardRepository.findByPuk(dto.getPuk()).isPresent()) {
            throw new RuntimeException("PUK already exists");
        }
        sim.setNumber(dto.getNumber());
        sim.setIccid(dto.getIccid());
        sim.setStatus(dto.getStatus());
        sim.setNotes(dto.getNotes());
        sim.setPin(dto.getPin());
        sim.setPuk(dto.getPuk());
        sim.setPoke(dto.getPoke());
        sim.setCarrier(dto.getCarrier());
        sim.setPlan(dto.getPlan());
        sim.setMonthlyFee(dto.getMonthlyFee());
        sim.setDataLimit(dto.getDataLimit());
        sim.setActivationDate(dto.getActivationDate());
        sim.setExpiryDate(dto.getExpiryDate());
        if (newUserId != null) {
            User user = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            sim.setAssignedTo(user);
            sim.setAssignedDate(dto.getAssignedDate() != null ? dto.getAssignedDate() : LocalDate.now());
        } else {
            sim.setAssignedTo(null);
            sim.setAssignedDate(null);
        }
        SimCard updated = simCardRepository.save(sim);
        // Record assignment history
        if (oldUserId == null && newUserId != null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.SIM,
                updated.getId(),
                null,
                newUserId,
                AssignmentHistory.Action.ASSIGN,
                "SIM assigned"
            );
        } else if (oldUserId != null && newUserId == null) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.SIM,
                updated.getId(),
                oldUserId,
                null,
                AssignmentHistory.Action.UNASSIGN,
                "SIM unassigned"
            );
        } else if (oldUserId != null && newUserId != null && !oldUserId.equals(newUserId)) {
            assignmentHistoryService.record(
                AssignmentHistory.Type.SIM,
                updated.getId(),
                oldUserId,
                newUserId,
                AssignmentHistory.Action.TRANSFER,
                "SIM transferred"
            );
        }
        return new SimCardDto(updated);
    }

    public void deleteSimCard(Long id) {
        if (!simCardRepository.existsById(id)) {
            throw new RuntimeException("SimCard not found");
        }
        simCardRepository.deleteById(id);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    public SimCardDto assignSimCard(Long simCardId, Long userId) {
        SimCard sim = simCardRepository.findById(simCardId)
            .orElseThrow(() -> new RuntimeException("SIM card not found"));
        
        if (sim.getAssignedTo() != null) {
            throw new RuntimeException("SIM card is already assigned to a user");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        sim.setAssignedTo(user);
        sim.setAssignedDate(LocalDate.now());
        sim.setStatus(SimCard.Status.ASSIGNED);
        SimCard saved = simCardRepository.save(sim);
        
        // Record assignment history
        assignmentHistoryService.record(
            AssignmentHistory.Type.SIM,
            saved.getId(),
            null,
            user.getId(),
            AssignmentHistory.Action.ASSIGN,
            "SIM card assigned via explicit endpoint"
        );
        
        return new SimCardDto(saved);
    }

    public SimCardDto unassignSimCard(Long simCardId) {
        SimCard sim = simCardRepository.findById(simCardId)
            .orElseThrow(() -> new RuntimeException("SIM card not found"));
        
        if (sim.getAssignedTo() == null) {
            throw new RuntimeException("SIM card is not assigned to any user");
        }
        
        Long oldUserId = sim.getAssignedTo().getId();
        sim.setAssignedTo(null);
        sim.setAssignedDate(null);
        sim.setStatus(SimCard.Status.AVAILABLE);
        SimCard saved = simCardRepository.save(sim);
        
        // Record unassignment history
        assignmentHistoryService.record(
            AssignmentHistory.Type.SIM,
            saved.getId(),
            oldUserId,
            null,
            AssignmentHistory.Action.UNASSIGN,
            "SIM card unassigned via explicit endpoint"
        );
        
        return new SimCardDto(saved);
    }

    public SimCardDto transferSimCard(Long simCardId, Long newUserId) {
        SimCard sim = simCardRepository.findById(simCardId)
            .orElseThrow(() -> new RuntimeException("SIM card not found"));
        
        if (sim.getAssignedTo() == null) {
            throw new RuntimeException("SIM card is not assigned to any user");
        }
        
        User newUser = userRepository.findById(newUserId)
            .orElseThrow(() -> new RuntimeException("New user not found"));
        
        Long oldUserId = sim.getAssignedTo().getId();
        sim.setAssignedTo(newUser);
        sim.setAssignedDate(LocalDate.now());
        sim.setStatus(SimCard.Status.ASSIGNED);
        SimCard saved = simCardRepository.save(sim);
        
        // Record transfer history
        assignmentHistoryService.record(
            AssignmentHistory.Type.SIM,
            saved.getId(),
            oldUserId,
            newUser.getId(),
            AssignmentHistory.Action.TRANSFER,
            "SIM card transferred via explicit endpoint"
        );
        
        return new SimCardDto(saved);
    }
} 