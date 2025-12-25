package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.notifications.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    List<Notification> findByUserId(Long userId);

    Optional<Notification> findById(Long id);

    void markAsRead(Long id);
}
