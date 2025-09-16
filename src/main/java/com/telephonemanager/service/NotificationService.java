package com.telephonemanager.service;

import com.telephonemanager.entity.Attribution;
import com.telephonemanager.entity.Attribution.Status;
import com.telephonemanager.entity.Phone;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.AttributionRepository;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.telephonemanager.entity.User.UserRole;

@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttributionRepository attributionRepository;

    /**
     * Returns computed notifications for the current user.
     * Currently implements: Phone renewal reminders based on active attributions (2-year cycle).
     * Buckets: 90 days, 60 days, 30 days, and overdue (<= 0).
     * Scope: ADMIN and ASSIGNER roles only.
     */
    public Map<String, Object> getUserNotifications(String userEmail, int page, int limit) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            result.put("notifications", List.of());
            result.put("pagination", Map.of(
                "page", page,
                "limit", limit,
                "total", 0,
                "totalPages", 0
            ));
            return result;
        }

        User user = userOpt.get();
        UserRole role = user.getRole();

        // Only Admin and Assigner receive phone renewal notifications
        boolean isPrivileged = role == UserRole.ADMIN || role == UserRole.ASSIGNER;
        if (!isPrivileged) {
            result.put("notifications", List.of());
            result.put("pagination", Map.of(
                "page", page,
                "limit", limit,
                "total", 0,
                "totalPages", 0
            ));
            return result;
        }

        // Fetch all ACTIVE attributions in a pagination-safe manner
        List<Attribution> activeAttributions = new ArrayList<>();
        int current = 0;
        int pageSize = 200;
        while (true) {
            Page<Attribution> p = attributionRepository.findByStatus(Status.ACTIVE, PageRequest.of(current, pageSize));
            if (p.getContent().isEmpty()) break;
            activeAttributions.addAll(p.getContent());
            if (current >= p.getTotalPages() - 1) break;
            current++;
        }

        LocalDate today = LocalDate.now();

        // Build bucketed renewal notifications with stable IDs to avoid duplicates
        List<Map<String, Object>> allNotifications = new ArrayList<>();
        for (Attribution attr : activeAttributions) {
            LocalDate assignmentDate = attr.getAssignmentDate() != null ? attr.getAssignmentDate() :
                    (attr.getCreatedAt() != null ? attr.getCreatedAt().toLocalDate() : today);
            LocalDate renewalDate = assignmentDate.plusYears(2);
            long daysUntil = ChronoUnit.DAYS.between(today, renewalDate);

            String bucket;
            if (daysUntil <= 0) {
                bucket = "OVERDUE"; // past due
            } else if (daysUntil <= 30) {
                bucket = "30";
            } else if (daysUntil <= 60) {
                bucket = "60";
            } else if (daysUntil <= 90) {
                bucket = "90";
            } else {
                continue; // outside tracked windows
            }

            Phone phone = attr.getPhone();
            String phoneLabel = phone != null ?
                    (phone.getBrand() != null ? phone.getBrand() : "") + " " + (phone.getModel() != null ? phone.getModel() : "") :
                    "Appareil";

            String title = bucket.equals("OVERDUE") ?
                    "Renouvellement de téléphone en retard" :
                    "Renouvellement de téléphone dans " + daysUntil + " jours";

            String message = String.format(
                    "%s – Utilisateur: %s | Département: %s | Échéance: %s",
                    phoneLabel.trim(),
                    attr.getUser() != null ? attr.getUser().getName() : "Utilisateur",
                    attr.getUser() != null ? (attr.getUser().getDepartment() != null ? attr.getUser().getDepartment() : "-") : "-",
                    renewalDate.toString()
            );

            String type = bucket.equals("OVERDUE") ? "warning" : (daysUntil <= 30 ? "warning" : "info");

            String stableId = "RENEWAL-ATTR-" + attr.getId() + "-B" + bucket;

            Map<String, Object> notif = new HashMap<>();
            notif.put("id", stableId);
            notif.put("title", title);
            notif.put("message", message);
            notif.put("type", type);
            notif.put("createdAt", LocalDateTime.now().toString());
            notif.put("read", false);
            // Route hint for frontend by role
            String route = role == UserRole.ASSIGNER ? "/assigner-dashboard/attributions" : "/admin-dashboard/attributions";
            notif.put("actionUrl", route);
            allNotifications.add(notif);
        }

        // Server-side pagination of computed notifications for consistency
        int total = allNotifications.size();
        int totalPages = (int) Math.ceil(total / (double) limit);
        int fromIndex = Math.max(0, Math.min((page - 1) * limit, total));
        int toIndex = Math.max(fromIndex, Math.min(fromIndex + limit, total));
        List<Map<String, Object>> paged = allNotifications.subList(fromIndex, toIndex);

        result.put("notifications", paged);
        result.put("pagination", Map.of(
            "page", page,
            "limit", limit,
            "total", total,
            "totalPages", totalPages
        ));
        return result;
    }

    public long getUnreadCount(String userEmail) {
        // As we do not persist notifications yet, return 0
        return 0;
    }

    public void markAsRead(Long notificationId, String userEmail) {
        // Not persisted yet
    }

    public void markAllAsRead(String userEmail) {
        // Not persisted yet
    }

    public void deleteNotification(Long notificationId, String userEmail) {
        // Not persisted yet
    }

    public void sendNotificationToUser(String title, String message, String type, Long userId) {
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                System.out.println("[NOTIFY] Sent to user " + user.get().getName() + " (ID: " + userId + "): " + title + " - " + message);
            } else {
                System.err.println("[NOTIFY][ERROR] User ID " + userId + " not found. Notification not sent.");
            }
        } else {
            System.err.println("[NOTIFY][ERROR] userId is null. Notification not sent.");
        }
    }

    public void sendNotificationToRole(String title, String message, String type, UserRole role) {
        List<User> users = userRepository.findByRole(role, PageRequest.of(0, 1000)).getContent();
        if (users.isEmpty()) {
            System.err.println("[NOTIFY][ERROR] No users found with role " + role + ". Notification not sent.");
            return;
        }
        for (User user : users) {
            sendNotificationToUser(title, message, type, user.getId());
        }
        System.out.println("[NOTIFY] Sent to all users with role " + role + ": " + title + " - " + message);
    }

    // Legacy method for backward compatibility
    public void sendNotification(String title, String message, String type, Long userId) {
        sendNotificationToUser(title, message, type, userId);
    }
}