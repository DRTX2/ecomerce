package com.drtx.ecomerce.amazon.core.ports.out.notification;

public interface EmailPort {
    void sendSimpleEmail(String to, String subject, String text);
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendPasswordResetEmail(String to, String token, String name);
    void sendWelcomeEmail(String to, String name);
    void sendOrderConfirmationEmail(String to, String name, String orderNumber);
}
