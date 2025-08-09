package com.telephonemanager.controller;

import com.telephonemanager.dto.SimCardDto;
import com.telephonemanager.entity.SimCard;
import com.telephonemanager.service.SimCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/simcards")
@Tag(name = "SIM Card Management", description = "SIM card management endpoints")
public class SimCardController {
    @Autowired
    private SimCardService simCardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get all SIM cards", description = "Get all SIM cards with pagination and filtering (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getSimCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) SimCard.Status status,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String iccid) {
        try {
            Page<SimCardDto> sims = simCardService.getSimCards(page, limit, status, number, iccid);
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", sims.getTotalElements());
            pagination.put("totalPages", sims.getTotalPages());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "simcards", sims.getContent(),
                "pagination", pagination
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SIM card by ID", description = "Get SIM card details by ID (Admin/Assigner/Assigned User only)")
    public ResponseEntity<Map<String, Object>> getSimCardById(@PathVariable Long id, Authentication authentication) {
        try {
            return simCardService.getSimCardById(id)
                    .map(sim -> {
                        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                        boolean isAssigner = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ASSIGNER"));
                        boolean isUser = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
                        String userEmail = authentication.getName();
                        boolean isOwner = sim.getAssignedToId() != null && sim.getAssignedToId().equals(simCardService.getUserIdByEmail(userEmail));
                        if (isAdmin || isAssigner || (isUser && isOwner)) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", true);
                            response.put("data", sim);
                            return ResponseEntity.ok(response);
                        } else {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", false);
                            response.put("error", Map.of(
                                "code", "FORBIDDEN",
                                "message", "Access denied"
                            ));
                            return ResponseEntity.status(403).body(response);
                        }
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", Map.of(
                            "code", "NOT_FOUND",
                            "message", "SIM card not found"
                        ));
                        return ResponseEntity.status(404).body(response);
                    });
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new SIM card", description = "Create a new SIM card (Admin only)")
    public ResponseEntity<Map<String, Object>> createSimCard(@Valid @RequestBody SimCardDto simCardDto, Authentication authentication) {
        System.out.println("=== SIM CARD CONTROLLER: Create SIM card request received");
        System.out.println("=== SIM CARD CONTROLLER: Authentication: " + authentication);
        if (authentication != null) {
            System.out.println("=== SIM CARD CONTROLLER: Principal: " + authentication.getName());
            System.out.println("=== SIM CARD CONTROLLER: Authorities: " + authentication.getAuthorities());
            System.out.println("=== SIM CARD CONTROLLER: Is authenticated: " + authentication.isAuthenticated());
        }
        System.out.println("=== SIM CARD CONTROLLER: SIM card data: " + simCardDto);
        try {
            SimCardDto createdSim = simCardService.createSimCard(simCardDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdSim);
            response.put("message", "SIM card created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println("=== SIM CARD CONTROLLER: Error creating SIM card: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "CREATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update SIM card", description = "Update SIM card information (Admin only)")
    public ResponseEntity<Map<String, Object>> updateSimCard(@PathVariable Long id, @Valid @RequestBody SimCardDto simCardDto) {
        try {
            SimCardDto updatedSim = simCardService.updateSimCard(id, simCardDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedSim);
            response.put("message", "SIM card updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "UPDATE_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete SIM card", description = "Delete SIM card (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteSimCard(@PathVariable Long id) {
        try {
            simCardService.deleteSimCard(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "SIM card deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "DELETE_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Assign SIM card to user", description = "Assign a SIM card to a specific user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> assignSimCard(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_USER_ID",
                    "message", "userId is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            SimCardDto assignedSim = simCardService.assignSimCard(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", assignedSim);
            response.put("message", "SIM card assigned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "ASSIGNMENT_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/unassign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Unassign SIM card from user", description = "Unassign a SIM card from its current user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> unassignSimCard(@PathVariable Long id) {
        try {
            SimCardDto unassignedSim = simCardService.unassignSimCard(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", unassignedSim);
            response.put("message", "SIM card unassigned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "UNASSIGNMENT_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Transfer SIM card to another user", description = "Transfer a SIM card from current user to another user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> transferSimCard(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long newUserId = request.get("newUserId");
            if (newUserId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_NEW_USER_ID",
                    "message", "newUserId is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            SimCardDto transferredSim = simCardService.transferSimCard(id, newUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transferredSim);
            response.put("message", "SIM card transferred successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "TRANSFER_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 