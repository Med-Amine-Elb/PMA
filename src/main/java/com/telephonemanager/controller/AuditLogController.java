package com.telephonemanager.controller;

import com.telephonemanager.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Logs", description = "Audit log viewing endpoints")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs", description = "Get audit logs with filtering and pagination (Admin only)")
    public ResponseEntity<Map<String, Object>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> logs = auditLogService.getAuditLogs(page, limit, action, entityType, userId, startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "AUDIT_LOG_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user audit logs", description = "Get audit logs for a specific user (Admin only)")
    public ResponseEntity<Map<String, Object>> getUserAuditLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            Map<String, Object> logs = auditLogService.getUserAuditLogs(userId, page, limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "AUDIT_LOG_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get entity audit logs", description = "Get audit logs for a specific entity (Admin only)")
    public ResponseEntity<Map<String, Object>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            Map<String, Object> logs = auditLogService.getEntityAuditLogs(entityType, entityId, page, limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "AUDIT_LOG_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit log summary", description = "Get audit log statistics and summary (Admin only)")
    public ResponseEntity<Map<String, Object>> getAuditLogSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> summary = auditLogService.getAuditLogSummary(startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "AUDIT_LOG_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export audit logs", description = "Export audit logs to CSV format (Admin only)")
    public ResponseEntity<Map<String, Object>> exportAuditLogs(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType) {
        try {
            String csvData = auditLogService.exportAuditLogs(startDate, endDate, action, entityType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "csvData", csvData,
                "filename", "audit_logs_" + System.currentTimeMillis() + ".csv"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "EXPORT_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 