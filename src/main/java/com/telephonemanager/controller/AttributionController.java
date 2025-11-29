package com.telephonemanager.controller;

import com.telephonemanager.dto.AttributionDto;
import com.telephonemanager.entity.Attribution.Status;
import com.telephonemanager.service.AttributionService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attributions")
@Tag(name = "Attribution Management", description = "Attribution management endpoints")
public class AttributionController {
    
    @Autowired
    private AttributionService attributionService;
    
    @Autowired
    private com.telephonemanager.service.AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get all attributions", description = "Get all attributions with pagination and filtering (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getAttributions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long assignedBy,
            @RequestParam(required = false) String search) {
        
        try {
            Page<AttributionDto> attributions = attributionService.getAttributions(page, limit, status, userId, assignedBy, search);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", attributions.getTotalElements());
            pagination.put("totalPages", attributions.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "attributions", attributions.getContent(),
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get attribution by ID", description = "Get attribution details by ID (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getAttributionById(@PathVariable Long id) {
        try {
            return attributionService.getAttributionById(id)
                    .map(attribution -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("data", attribution);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", Map.of(
                            "code", "NOT_FOUND",
                            "message", "Attribution not found"
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Create new attribution", description = "Create a new attribution (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> createAttribution(
            @Valid @RequestBody AttributionDto attributionDto,
            Authentication authentication) {
        System.out.println("=== ATTRIBUTION CONTROLLER: Create attribution request received");
        System.out.println("=== ATTRIBUTION CONTROLLER: Authentication: " + authentication);
        if (authentication != null) {
            System.out.println("=== ATTRIBUTION CONTROLLER: Principal: " + authentication.getName());
            System.out.println("=== ATTRIBUTION CONTROLLER: Authorities: " + authentication.getAuthorities());
            System.out.println("=== ATTRIBUTION CONTROLLER: Is authenticated: " + authentication.isAuthenticated());
        }
        System.out.println("=== ATTRIBUTION CONTROLLER: Attribution data: " + attributionDto);
        System.out.println("=== ATTRIBUTION CONTROLLER: UserId: " + attributionDto.getUserId());
        System.out.println("=== ATTRIBUTION CONTROLLER: PhoneId: " + attributionDto.getPhoneId());
        System.out.println("=== ATTRIBUTION CONTROLLER: SimCardId: " + attributionDto.getSimCardId());
        System.out.println("=== ATTRIBUTION CONTROLLER: Status: " + attributionDto.getStatus());
        try {
            // Get current user ID
            Long assignedById = authService.getUserIdByEmail(authentication.getName());
            System.out.println("=== ATTRIBUTION CONTROLLER: AssignedById: " + assignedById);
            
            AttributionDto createdAttribution = attributionService.createAttribution(attributionDto, assignedById);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdAttribution);
            response.put("message", "Attribution created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println("=== ATTRIBUTION CONTROLLER: Error creating attribution: " + e.getMessage());
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Update attribution", description = "Update attribution information (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> updateAttribution(
            @PathVariable Long id,
            @Valid @RequestBody AttributionDto attributionDto) {
        System.out.println("=== ATTRIBUTION CONTROLLER: Update attribution request received");
        System.out.println("=== ATTRIBUTION CONTROLLER: Attribution ID: " + id);
        System.out.println("=== ATTRIBUTION CONTROLLER: Attribution data: " + attributionDto);
        System.out.println("=== ATTRIBUTION CONTROLLER: Status: " + attributionDto.getStatus());
        System.out.println("=== ATTRIBUTION CONTROLLER: Notes: " + attributionDto.getNotes());
        try {
            AttributionDto updatedAttribution = attributionService.updateAttribution(id, attributionDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedAttribution);
            response.put("message", "Attribution updated successfully");
            
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
    @Operation(summary = "Delete attribution", description = "Delete attribution (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteAttribution(@PathVariable Long id) {
        try {
            attributionService.deleteAttribution(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Attribution deleted successfully");
            
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

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Return attribution", description = "Mark attribution as returned (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> returnAttribution(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {
        try {
            String notes = request != null ? request.get("notes") : null;
            Long actorId = authentication != null ? authService.getUserIdByEmail(authentication.getName()) : null;
            AttributionDto returnedAttribution = attributionService.returnAttribution(id, notes, actorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", returnedAttribution);
            response.put("message", "Attribution returned successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "RETURN_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/history/sim/{simCardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get SIM card attribution history", description = "Get attribution history for a specific SIM card (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getAttributionHistoryBySimCard(@PathVariable Long simCardId) {
        try {
            List<AttributionDto> history = attributionService.getAttributionHistoryBySimCard(simCardId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "history", history,
                "total", history.size()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "HISTORY_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/history/phone/{phoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get phone attribution history", description = "Get attribution history for a specific phone (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getAttributionHistoryByPhone(@PathVariable Long phoneId) {
        try {
            List<AttributionDto> history = attributionService.getAttributionHistoryByPhone(phoneId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "history", history,
                "total", history.size()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "HISTORY_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get user active attributions", description = "Get active attributions for a specific user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getActiveAttributionsByUser(@PathVariable Long userId) {
        try {
            List<AttributionDto> attributions = attributionService.getActiveAttributionsByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "attributions", attributions,
                "total", attributions.size()
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
} 