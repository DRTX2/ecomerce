package com.drtx.ecomerce.amazon.infrastructure.email;

import com.drtx.ecomerce.amazon.core.model.exceptions.NotificationException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmailTestConfig.class)
class JavaMailEmailAdapterIntegrationTest {

    @Autowired
    private JavaMailSender mailSender;

    private EmailTemplateLoader templateLoader;
    private JavaMailEmailAdapter emailAdapter;

    @BeforeEach
    void setUp() {
        reset(mailSender);

        // Mock del template loader
        templateLoader = mock(EmailTemplateLoader.class);

        emailAdapter = new JavaMailEmailAdapter(mailSender, templateLoader);
        setFieldValue(emailAdapter, "fromEmail", "test@example.com");
    }

    @Test
    void testSendSimpleEmail_Success() {
        String to = "destinatario@example.com";
        String subject = "Test Subject";
        String text = "Este es un correo de prueba";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailAdapter.sendSimpleEmail(to, subject, text));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendHtmlEmail_Success() throws MessagingException {
        String to = "destinatario@example.com";
        String subject = "Test HTML Subject";
        String htmlContent = "<h1>Hola</h1><p>Este es un correo HTML</p>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailAdapter.sendHtmlEmail(to, subject, htmlContent));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendPasswordResetEmail_Success() throws MessagingException {
        String to = "usuario@example.com";
        String token = "ABC123XYZ";
        String name = "David";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Mock del template loader
        when(templateLoader.loadTemplate(eq("password-reset"), any()))
                .thenReturn("<html><body>Password reset email</body></html>");

        assertDoesNotThrow(() -> emailAdapter.sendPasswordResetEmail(to, token, name));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateLoader, times(1)).loadTemplate(eq("password-reset"), any());
    }

    @Test
    void testSendWelcomeEmail_Success() throws MessagingException {
        String to = "nuevousuario@example.com";
        String name = "María";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        when(templateLoader.loadTemplate(eq("welcome"), any()))
                .thenReturn("<html><body>Welcome email</body></html>");

        assertDoesNotThrow(() -> emailAdapter.sendWelcomeEmail(to, name));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateLoader, times(1)).loadTemplate(eq("welcome"), any());
    }

    @Test
    void testSendOrderConfirmationEmail_Success() throws MessagingException {
        String to = "cliente@example.com";
        String name = "Carlos";
        String orderNumber = "ORD-2024-12345";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        when(templateLoader.loadTemplate(eq("order-confirmation"), any()))
                .thenReturn("<html><body>Order confirmation email</body></html>");

        assertDoesNotThrow(() -> emailAdapter.sendOrderConfirmationEmail(to, name, orderNumber));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateLoader, times(1)).loadTemplate(eq("order-confirmation"), any());
    }

    @Test
    void testSendSimpleEmail_WithException_ThrowsNotificationException() {
        String to = "destinatario@example.com";
        String subject = "Test Subject";
        String text = "Test content";

        doThrow(new MailSendException("Simulated error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(NotificationException.class,
                () -> emailAdapter.sendSimpleEmail(to, subject, text));
    }

    @Test
    void testSendHtmlEmail_WithException_ThrowsNotificationException() throws MessagingException {
        String to = "destinatario@example.com";
        String subject = "Test HTML Subject";
        String htmlContent = "<p>Test</p>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Simulated error"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThrows(NotificationException.class,
                () -> emailAdapter.sendHtmlEmail(to, subject, htmlContent));
    }

    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }
}
