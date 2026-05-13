package com.petadoption.system.controller;

import com.petadoption.system.dto.ApiResponse;
import com.petadoption.system.model.Notification;
import com.petadoption.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get user's notifications
     * GET /api/notifications/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(
            @PathVariable Long userId
    ) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    /**
     * Get unread notifications count
     * GET /api/notifications/{userId}/unread-count
     */
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
    }

    /**
     * Mark notification as read
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }

    /**
     * Mark all notifications as read
     * PUT /api/notifications/{userId}/read-all
     */
    @PutMapping("/{userId}/read-all")
    public ResponseEntity<ApiResponse<Object>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
