package com.telephonemanager.controller;

import com.telephonemanager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard & Reporting", description = "Dashboard statistics and reporting endpoints")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get dashboard overview", description = "Get overall statistics for dashboard (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        try {
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", overview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "DASHBOARD_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/phones/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get phone statistics", description = "Get detailed phone statistics (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getPhoneStats() {
        try {
            Map<String, Object> stats = dashboardService.getPhoneStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "STATS_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/simcards/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get SIM card statistics", description = "Get detailed SIM card statistics (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getSimCardStats() {
        try {
            Map<String, Object> stats = dashboardService.getSimCardStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "STATS_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get user statistics", description = "Get user statistics and distribution (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = dashboardService.getUserStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "STATS_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get recent activity", description = "Get recent assignments, transfers, and activities (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> activity = dashboardService.getRecentActivity(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "ACTIVITY_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get system alerts", description = "Get items needing attention (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getAlerts() {
        try {
            Map<String, Object> alerts = dashboardService.getAlerts();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", alerts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "ALERTS_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 