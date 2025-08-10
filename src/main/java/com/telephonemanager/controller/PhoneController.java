package com.telephonemanager.controller;

import com.telephonemanager.dto.PhoneDto;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.service.PhoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/phones")
@Tag(name = "Phone Management", description = "Phone management endpoints")
public class PhoneController {
    @Autowired
    private PhoneService phoneService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get all phones", description = "Get all phones with pagination and filtering (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getPhones(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Phone.Status status,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {
        try {
            Page<PhoneDto> phones = phoneService.getPhones(page, limit, status, brand, model);
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", phones.getTotalElements());
            pagination.put("totalPages", phones.getTotalPages());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "phones", phones.getContent(),
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
    @Operation(summary = "Get phone by ID", description = "Get phone details by ID (Admin/Assigner/Assigned User only)")
    public ResponseEntity<Map<String, Object>> getPhoneById(@PathVariable Long id, Authentication authentication) {
        try {
            return phoneService.getPhoneById(id)
                    .map(phone -> {
                        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                        boolean isAssigner = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ASSIGNER"));
                        boolean isUser = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
                        String userEmail = authentication.getName();
                        boolean isOwner = phone.getAssignedToId() != null && phone.getAssignedToId().equals(phoneService.getUserIdByEmail(userEmail));
                        if (isAdmin || isAssigner || (isUser && isOwner)) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("success", true);
                            response.put("data", phone);
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
                            "message", "Phone not found"
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

    @GetMapping("/{id}/usage-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER') or hasRole('USER')")
    @Operation(summary = "Get phone usage statistics", description = "Get phone usage statistics by phone ID (Admin/Assigner/Assigned User only)")
    public ResponseEntity<Map<String, Object>> getPhoneUsageStats(@PathVariable Long id, Authentication authentication) {
        System.out.println("[DEBUG] Entered getPhoneUsageStats for phone ID: " + id);
        System.out.println("[DEBUG] Authentication object: " + authentication);
        // For now, allow only admin, assigner, or assigned user
        try {
            PhoneDto phone = phoneService.getPhoneById(id).orElse(null);
            if (phone == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "NOT_FOUND",
                    "message", "Phone not found"
                ));
                return ResponseEntity.status(404).body(response);
            }
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAssigner = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ASSIGNER"));
            boolean isUser = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
            String userEmail = authentication.getName();
            boolean isOwner = phone.getAssignedToId() != null && phone.getAssignedToId().equals(phoneService.getUserIdByEmail(userEmail));
            if (!(isAdmin || isAssigner || (isUser && isOwner))) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "FORBIDDEN",
                    "message", "Access denied"
                ));
                return ResponseEntity.status(403).body(response);
            }
            Map<String, Object> stats = Map.of(
                "callsThisMonth", 127,
                "smsThisMonth", 89,
                "dataUsedGB", 12.5,
                "dataLimitGB", 50,
                "averageCallDuration", "3m 45s",
                "mostUsedApp", "Teams",
                "screenTime", "6h 32m",
                "batteryHealth", 98,
                "storageUsed", 45
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "USAGE_STATS_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}/maintenance-history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER') or hasRole('USER')")
    @Operation(summary = "Get phone maintenance history", description = "Get phone maintenance history by phone ID (Admin/Assigner/Assigned User only)")
    public ResponseEntity<Map<String, Object>> getPhoneMaintenanceHistory(@PathVariable Long id, Authentication authentication) {
        // TODO: Replace with real data fetching logic
        try {
            PhoneDto phone = phoneService.getPhoneById(id).orElse(null);
            if (phone == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "NOT_FOUND",
                    "message", "Phone not found"
                ));
                return ResponseEntity.status(404).body(response);
            }
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAssigner = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ASSIGNER"));
            boolean isUser = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
            String userEmail = authentication.getName();
            boolean isOwner = phone.getAssignedToId() != null && phone.getAssignedToId().equals(phoneService.getUserIdByEmail(userEmail));
            if (!(isAdmin || isAssigner || (isUser && isOwner))) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "FORBIDDEN",
                    "message", "Access denied"
                ));
                return ResponseEntity.status(403).body(response);
            }
            // Mock maintenance history data
            var history = java.util.List.of(
                Map.of(
                    "id", "maint_1",
                    "date", "2024-01-15",
                    "type", "Mise à jour logicielle",
                    "description", "iOS 17.1.2 - Corrections de sécurité",
                    "status", "Terminé",
                    "technician", "Jean Dupont"
                )
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "MAINTENANCE_HISTORY_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new phone", description = "Create a new phone (Admin only)")
    public ResponseEntity<Map<String, Object>> createPhone(@Valid @RequestBody PhoneDto phoneDto, Authentication authentication) {
        System.out.println("=== PHONE CONTROLLER: Create phone request received");
        System.out.println("=== PHONE CONTROLLER: Authentication: " + authentication);
        if (authentication != null) {
            System.out.println("=== PHONE CONTROLLER: Principal: " + authentication.getName());
            System.out.println("=== PHONE CONTROLLER: Authorities: " + authentication.getAuthorities());
            System.out.println("=== PHONE CONTROLLER: Is authenticated: " + authentication.isAuthenticated());
        }
        
        try {
            PhoneDto createdPhone = phoneService.createPhone(phoneDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdPhone);
            response.put("message", "Phone created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println("=== PHONE CONTROLLER: Error creating phone: " + e.getMessage());
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
    @Operation(summary = "Update phone", description = "Update phone information (Admin only)")
    public ResponseEntity<Map<String, Object>> updatePhone(@PathVariable Long id, @Valid @RequestBody PhoneDto phoneDto) {
        try {
            PhoneDto updatedPhone = phoneService.updatePhone(id, phoneDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedPhone);
            response.put("message", "Phone updated successfully");
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
    @Operation(summary = "Delete phone", description = "Delete phone (Admin only)")
    public ResponseEntity<Map<String, Object>> deletePhone(@PathVariable Long id) {
        try {
            phoneService.deletePhone(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phone deleted successfully");
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
    @Operation(summary = "Assign phone to user", description = "Assign a phone to a specific user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> assignPhone(@PathVariable Long id, @RequestBody Map<String, Long> request) {
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
            
            PhoneDto assignedPhone = phoneService.assignPhone(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", assignedPhone);
            response.put("message", "Phone assigned successfully");
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
    @Operation(summary = "Unassign phone from user", description = "Unassign a phone from its current user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> unassignPhone(@PathVariable Long id) {
        try {
            PhoneDto unassignedPhone = phoneService.unassignPhone(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", unassignedPhone);
            response.put("message", "Phone unassigned successfully");
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
    @Operation(summary = "Transfer phone to another user", description = "Transfer a phone from current user to another user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> transferPhone(@PathVariable Long id, @RequestBody Map<String, Long> request) {
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
            
            PhoneDto transferredPhone = phoneService.transferPhone(id, newUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", transferredPhone);
            response.put("message", "Phone transferred successfully");
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

    @PostMapping("/{id}/maintenance-request")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Request maintenance for a phone", description = "User can request maintenance for their assigned phone")
    public ResponseEntity<Map<String, Object>> requestMaintenance(
        @PathVariable Long id,
        @RequestBody Map<String, String> requestBody,
        Authentication authentication
    ) {
        String description = requestBody.get("description");
        String urgency = requestBody.get("urgency");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Maintenance request submitted for review.");
        response.put("data", Map.of(
            "phoneId", id,
            "requestedBy", authentication.getName(),
            "description", description,
            "urgency", urgency,
            "status", "PENDING"
        ));
        return ResponseEntity.ok(response);
    }
} 