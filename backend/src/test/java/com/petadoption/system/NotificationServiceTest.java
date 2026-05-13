package com.petadoption.system;

import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.Notification;
import com.petadoption.system.repository.NotificationRepository;
import com.petadoption.system.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService.
 * Tests cover creating, retrieving, and marking notifications as read.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification unreadNotification;
    private Notification readNotification;

    @BeforeEach
    void setUp() {
        // Unread notification
        unreadNotification = new Notification();
        unreadNotification.setId(1L);
        unreadNotification.setUserId(2L);
        unreadNotification.setMessage("Your adoption request for Buddy has been submitted!");
        unreadNotification.setType(Notification.NotificationType.SYSTEM_MESSAGE);
        unreadNotification.setIsRead(false);

        // Read notification
        readNotification = new Notification();
        readNotification.setId(2L);
        readNotification.setUserId(2L);
        readNotification.setMessage("Congratulations! Your adoption request has been approved!");
        readNotification.setType(Notification.NotificationType.ADOPTION_APPROVED);
        readNotification.setIsRead(true);
    }

    // ===================== CREATE NOTIFICATION TESTS =====================

    @Test
    @DisplayName("CreateNotification - Success: notification is saved with correct fields")
    void createNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(unreadNotification);

        Notification result = notificationService.createNotification(
                2L,
                "Your adoption request for Buddy has been submitted!",
                Notification.NotificationType.SYSTEM_MESSAGE
        );

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals(Notification.NotificationType.SYSTEM_MESSAGE, result.getType());
        assertFalse(result.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("CreateNotification - ADOPTION_APPROVED type is saved correctly")
    void createNotification_ApprovalType_Success() {
        Notification approvalNotif = new Notification();
        approvalNotif.setId(3L);
        approvalNotif.setUserId(2L);
        approvalNotif.setMessage("Congratulations! Your adoption request has been approved!");
        approvalNotif.setType(Notification.NotificationType.ADOPTION_APPROVED);
        approvalNotif.setIsRead(false);

        when(notificationRepository.save(any(Notification.class))).thenReturn(approvalNotif);

        Notification result = notificationService.createNotification(
                2L,
                "Congratulations! Your adoption request has been approved!",
                Notification.NotificationType.ADOPTION_APPROVED
        );

        assertEquals(Notification.NotificationType.ADOPTION_APPROVED, result.getType());
    }

    @Test
    @DisplayName("CreateNotification - ADOPTION_REJECTED type is saved correctly")
    void createNotification_RejectionType_Success() {
        Notification rejectionNotif = new Notification();
        rejectionNotif.setId(4L);
        rejectionNotif.setUserId(2L);
        rejectionNotif.setMessage("Unfortunately, your adoption request has been rejected.");
        rejectionNotif.setType(Notification.NotificationType.ADOPTION_REJECTED);
        rejectionNotif.setIsRead(false);

        when(notificationRepository.save(any(Notification.class))).thenReturn(rejectionNotif);

        Notification result = notificationService.createNotification(
                2L,
                "Unfortunately, your adoption request has been rejected.",
                Notification.NotificationType.ADOPTION_REJECTED
        );

        assertEquals(Notification.NotificationType.ADOPTION_REJECTED, result.getType());
    }

    // ===================== GET NOTIFICATIONS TESTS =====================

    @Test
    @DisplayName("GetUserNotifications - Returns all notifications for a user")
    void getUserNotifications_ReturnsAllForUser() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(readNotification, unreadNotification));

        List<Notification> notifications = notificationService.getUserNotifications(2L);

        assertEquals(2, notifications.size());
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(2L);
    }

    @Test
    @DisplayName("GetUserNotifications - Returns empty list when user has no notifications")
    void getUserNotifications_EmptyList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(99L))
                .thenReturn(List.of());

        List<Notification> notifications = notificationService.getUserNotifications(99L);

        assertTrue(notifications.isEmpty());
    }

    @Test
    @DisplayName("GetUnreadNotifications - Returns only unread notifications")
    void getUnreadNotifications_ReturnsOnlyUnread() {
        when(notificationRepository.findByUserIdAndIsRead(2L, false))
                .thenReturn(List.of(unreadNotification));

        List<Notification> unread = notificationService.getUnreadNotifications(2L);

        assertEquals(1, unread.size());
        assertFalse(unread.get(0).getIsRead());
    }

    // ===================== MARK AS READ TESTS =====================

    @Test
    @DisplayName("MarkAsRead - Success: notification isRead set to true")
    void markAsRead_Success() {
        Notification markedRead = new Notification();
        markedRead.setId(1L);
        markedRead.setUserId(2L);
        markedRead.setIsRead(true);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(unreadNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(markedRead);

        Notification result = notificationService.markAsRead(1L);

        assertTrue(result.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("MarkAsRead - Fail: throws ResourceNotFoundException when notification not found")
    void markAsRead_NotFound_ThrowsResourceNotFoundException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99L)
        );

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // ===================== MARK ALL AS READ TESTS =====================

    @Test
    @DisplayName("MarkAllAsRead - Success: all unread notifications are marked as read")
    void markAllAsRead_Success() {
        Notification unread2 = new Notification();
        unread2.setId(3L);
        unread2.setUserId(2L);
        unread2.setIsRead(false);

        when(notificationRepository.findByUserIdAndIsRead(2L, false))
                .thenReturn(Arrays.asList(unreadNotification, unread2));
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        notificationService.markAllAsRead(2L);

        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("MarkAllAsRead - No action when user has no unread notifications")
    void markAllAsRead_NoUnread_NoSaveCalled() {
        when(notificationRepository.findByUserIdAndIsRead(2L, false))
                .thenReturn(List.of());

        notificationService.markAllAsRead(2L);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // ===================== COUNT UNREAD TESTS =====================

    @Test
    @DisplayName("CountUnread - Returns correct count of unread notifications")
    void countUnread_ReturnsCorrectCount() {
        when(notificationRepository.countByUserIdAndIsRead(2L, false)).thenReturn(3L);

        long count = notificationService.countUnread(2L);

        assertEquals(3L, count);
    }

    @Test
    @DisplayName("CountUnread - Returns zero when no unread notifications")
    void countUnread_ReturnsZero() {
        when(notificationRepository.countByUserIdAndIsRead(2L, false)).thenReturn(0L);

        long count = notificationService.countUnread(2L);

        assertEquals(0L, count);
    }
}
