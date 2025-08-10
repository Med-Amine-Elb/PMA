package com.telephonemanager.controller;

import com.telephonemanager.dto.RequestDto;
import com.telephonemanager.entity.Request;
import com.telephonemanager.entity.Request.Priority;
import com.telephonemanager.entity.Request.Status;
import com.telephonemanager.entity.Request.Type;
import com.telephonemanager.service.RequestService;
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
@RequestMapping("/requests")
@Tag(name = "Request Management", description = "Request management endpoints")
public class RequestController {
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private com.telephonemanager.service.AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get all requests", description = "Get all requests with pagination and filtering (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String search) {
        
        try {
            Page<RequestDto> requests = requestService.getRequests(page, limit, status, type, priority, userId, assignedTo, search);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", requests.getTotalElements());
            pagination.put("totalPages", requests.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "requests", requests.getContent(),
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
    @Operation(summary = "Get request by ID", description = "Get request details by ID")
    public ResponseEntity<Map<String, Object>> getRequestById(@PathVariable Long id) {
        try {
            return requestService.getRequestById(id)
                    .map(request -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("data", request);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", Map.of(
                            "code", "NOT_FOUND",
                            "message", "Request not found"
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
    @Operation(summary = "Create new request", description = "Create a new request")
    public ResponseEntity<Map<String, Object>> createRequest(
            @Valid @RequestBody RequestDto requestDto,
            Authentication authentication) {
        try {
            // Get current user ID
            Long userId = authService.getUserIdByEmail(authentication.getName());
            
            RequestDto createdRequest = requestService.createRequest(requestDto, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdRequest);
            response.put("message", "Request created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
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
    @Operation(summary = "Update request", description = "Update request information (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RequestDto requestDto) {
        try {
            RequestDto updatedRequest = requestService.updateRequest(id, requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedRequest);
            response.put("message", "Request updated successfully");
            
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
    @Operation(summary = "Delete request", description = "Delete request (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteRequest(@PathVariable Long id) {
        try {
            requestService.deleteRequest(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Request deleted successfully");
            
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

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Add comment to request", description = "Add a comment to a request (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String comment = request.get("comment");
            if (comment == null || comment.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_COMMENT",
                    "message", "Comment is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            RequestDto updatedRequest = requestService.addComment(id, comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedRequest);
            response.put("message", "Comment added successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "COMMENT_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user requests", description = "Get all requests for a specific user")
    public ResponseEntity<Map<String, Object>> getRequestsByUser(@PathVariable Long userId) {
        try {
            List<RequestDto> requests = requestService.getRequestsByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "requests", requests,
                "total", requests.size()
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

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get pending requests", description = "Get all pending requests (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getPendingRequests() {
        try {
            List<RequestDto> requests = requestService.getPendingRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "requests", requests,
                "total", requests.size()
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

    @GetMapping("/urgent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get urgent requests", description = "Get all urgent requests (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUrgentRequests() {
        try {
            List<RequestDto> requests = requestService.getUrgentRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "requests", requests,
                "total", requests.size()
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

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Assign request", description = "Assign a request to a user (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> assignRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        try {
            Long assignedToId = request.get("assignedToId");
            if (assignedToId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_ASSIGNED_TO",
                    "message", "assignedToId is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            RequestDto assignedRequest = requestService.assignRequest(id, assignedToId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", assignedRequest);
            response.put("message", "Request assigned successfully");
            
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

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Resolve request", description = "Mark a request as resolved (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> resolveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String resolution = request.get("resolution");
            if (resolution == null || resolution.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_RESOLUTION",
                    "message", "Resolution is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            RequestDto resolvedRequest = requestService.resolveRequest(id, resolution);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", resolvedRequest);
            response.put("message", "Request resolved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "RESOLUTION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Reject request", description = "Reject a request (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_REASON",
                    "message", "Reason is required"
                ));
                return ResponseEntity.badRequest().body(response);
            }
            
            RequestDto rejectedRequest = requestService.rejectRequest(id, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rejectedRequest);
            response.put("message", "Request rejected successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "REJECTION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 