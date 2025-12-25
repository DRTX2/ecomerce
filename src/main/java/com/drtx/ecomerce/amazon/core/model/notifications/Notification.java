package com.drtx.ecomerce.amazon.core.model.notifications;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private User user;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(Long id, User user, String message, NotificationType type, NotificationStatus status,
            LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
