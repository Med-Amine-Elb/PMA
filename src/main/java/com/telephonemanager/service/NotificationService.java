package com.telephonemanager.service;

import com.telephonemanager.entity.User;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.telephonemanager.entity.User.UserRole;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getUserNotifications(String userEmail, int page, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        // For now, return mock data since we haven't implemented the Notification entity yet
        // In a real implementation, you would query the notification repository
        
        result.put("notifications", new Object[0]); // Empty array for now
        result.put("pagination", Map.of(
            "page", page,
            "limit", limit,
            "total", 0,
            "totalPages", 0
        ));
        
        return result;
    }

    public long getUnreadCount(String userEmail) {
        // For now, return 0 since we haven't implemented the Notification entity yet
        return 0;
    }

    public void markAsRead(Long notificationId, String userEmail) {
        // For now, do nothing since we haven't implemented the Notification entity yet
        // In a real implementation, you would update the notification status
    }

    public void markAllAsRead(String userEmail) {
        // For now, do nothing since we haven't implemented the Notification entity yet
        // In a real implementation, you would update all notifications for the user
    }

    public void deleteNotification(Long notificationId, String userEmail) {
        // For now, do nothing since we haven't implemented the Notification entity yet
        // In a real implementation, you would delete the notification
    }

    public void sendNotificationToUser(String title, String message, String type, Long userId) {
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                System.out.println("[NOTIFY] Sent to user " + user.get().getName() + " (ID: " + userId + "): " + title + " - " + message);
                // Note: WebSocket functionality removed - notifications are now logged only
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