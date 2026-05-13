package com.petadoption.system.repository;

import com.petadoption.system.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a specific user
    List<Notification> findByUserId(Long userId);

    // Find all notifications for a user ordered by date (newest first)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find unread notifications for a user
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);

    // Count unread notifications for a user
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
