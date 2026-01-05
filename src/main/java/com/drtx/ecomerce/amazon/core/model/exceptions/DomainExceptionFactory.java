package com.drtx.ecomerce.amazon.core.model.exceptions;

/**
 * Factory para crear excepciones de dominio con mensajes estandarizados.
 * Nivel Senior: Centraliza la creación de excepciones siguiendo el patrón
 * Factory.
 */
public final class DomainExceptionFactory {

    private DomainExceptionFactory() {
        // Utility class
    }

    // ========== Entity Not Found ==========

    public static EntityNotFoundException productNotFound(Long productId) {
        return new EntityNotFoundException("Product", productId);
    }

    public static EntityNotFoundException userNotFound(Long userId) {
        return new EntityNotFoundException("User", userId);
    }

    public static EntityNotFoundException userNotFoundByEmail(String email) {
        return new EntityNotFoundException(
                String.format("User with email '%s' not found", email));
    }

    public static EntityNotFoundException orderNotFound(Long orderId) {
        return new EntityNotFoundException("Order", orderId);
    }

    public static EntityNotFoundException categoryNotFound(Long categoryId) {
        return new EntityNotFoundException("Category", categoryId);
    }

    public static EntityNotFoundException cartNotFound(Long cartId) {
        return new EntityNotFoundException("Cart", cartId);
    }

    public static EntityNotFoundException appealNotFound(Long appealId) {
        return new EntityNotFoundException("Appeal", appealId);
    }

    public static EntityNotFoundException incidenceNotFound(Long incidenceId) {
        return new EntityNotFoundException("Incidence", incidenceId);
    }

    // ========== Business Rule Violations ==========

    public static DomainException invalidPassword(String reason) {
        return new DomainException("Invalid password: " + reason);
    }

    public static DomainException passwordCannotContainEmail() {
        return new DomainException("Password cannot contain the email address");
    }

    public static DomainException invalidProductPrice() {
        return new DomainException("Product price must be greater than zero");
    }

    public static DomainException invalidStock() {
        return new DomainException("Stock quantity cannot be negative");
    }

    public static DomainException orderAlreadyProcessed(Long orderId) {
        return new DomainException(
                String.format("Order %d has already been processed", orderId));
    }

    public static DomainException insufficientStock(String productName, int available) {
        return new DomainException(
                String.format("Insufficient stock for product '%s'. Available: %d", productName, available));
    }

    // ========== Storage Exceptions ==========

    public static StorageException imageUploadFailed(String fileName, Throwable cause) {
        return new StorageException(
                String.format("Failed to upload image: %s", fileName), cause);
    }

    public static StorageException imageDeleteFailed(String fileName, Throwable cause) {
        return new StorageException(
                String.format("Failed to delete image: %s", fileName), cause);
    }

    public static StorageException invalidImageFormat(String contentType) {
        return new StorageException(
                String.format("Invalid image format: %s", contentType));
    }

    public static StorageException imageTooLarge(long size, long maxSize) {
        return new StorageException(
                String.format("Image size (%d bytes) exceeds maximum allowed (%d bytes)", size, maxSize));
    }

    public static StorageException tooManyImages(int count, int maxCount) {
        return new StorageException(
                String.format("Number of images (%d) exceeds maximum allowed (%d)", count, maxCount));
    }

    // ========== Notification Exceptions ==========

    public static NotificationException emailSendFailed(String recipient, Throwable cause) {
        return new NotificationException(
                String.format("Failed to send email to: %s", recipient), cause);
    }

    public static NotificationException invalidEmailTemplate(String templateName) {
        return new NotificationException(
                String.format("Invalid email template: %s", templateName));
    }
}
