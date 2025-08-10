package com.telephonemanager.controller;

import com.telephonemanager.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get notifications for the current user")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            String userEmail = authentication.getName();
            Map<String, Object> notifications = notificationService.getUserNotifications(userEmail, page, limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications count", description = "Get count of unread notifications for current user")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            long unreadCount = notificationService.getUnreadCount(userEmail);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of("unreadCount", unreadCount));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            notificationService.markAsRead(id, userEmail);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for current user")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            notificationService.markAllAsRead(userEmail);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All notifications marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            notificationService.deleteNotification(id, userEmail);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send notification", description = "Send a notification to a user or all users of a role (Admin/Assigner/User)")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            String type = (String) request.get("type");
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            String targetRole = request.get("targetRole") != null ? request.get("targetRole").toString() : null;

            if (title == null || message == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "MISSING_FIELDS",
                    "message", "Title and message are required"
                ));
                return ResponseEntity.badRequest().body(response);
            }

            if (userId != null) {
                notificationService.sendNotificationToUser(title, message, type, userId);
                System.out.println("[DEBUG] Notification sent to userId: " + userId);
            } else if (targetRole != null) {
                switch (targetRole.toUpperCase()) {
                    case "ADMIN":
                        notificationService.sendNotificationToRole(title, message, type, com.telephonemanager.entity.User.UserRole.ADMIN);
                        System.out.println("[DEBUG] Notification sent to all ADMINs");
                        break;
                    case "ASSIGNER":
                        notificationService.sendNotificationToRole(title, message, type, com.telephonemanager.entity.User.UserRole.ASSIGNER);
                        System.out.println("[DEBUG] Notification sent to all ASSIGNERs");
                        break;
                    case "USER":
                        notificationService.sendNotificationToRole(title, message, type, com.telephonemanager.entity.User.UserRole.USER);
                        System.out.println("[DEBUG] Notification sent to all USERs");
                        break;
                    default:
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", Map.of(
                            "code", "INVALID_ROLE",
                            "message", "Unknown targetRole: " + targetRole
                        ));
                        return ResponseEntity.badRequest().body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "NO_TARGET",
                    "message", "You must specify either userId or targetRole (ADMIN, ASSIGNER, USER)"
                ));
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOTIFICATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 