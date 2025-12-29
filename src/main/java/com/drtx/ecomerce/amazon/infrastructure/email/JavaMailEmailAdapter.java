package com.drtx.ecomerce.amazon.infrastructure.email;

import com.drtx.ecomerce.amazon.core.model.exceptions.NotificationException;
import com.drtx.ecomerce.amazon.core.ports.out.notification.EmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Adaptador para envío de correos electrónicos implementando el puerto
 * EmailPort utilizando JavaMailSender.
 * Utiliza JavaMailSender para realizar el envío y EmailTemplateLoader para
 * cargar plantillas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "email.provider", havingValue = "javamail", matchIfMissing = true)
public class JavaMailEmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;
    private final EmailTemplateLoader templateLoader;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Correo simple enviado exitosamente a: {}", to);
        } catch (MailException e) {
            log.error("Error al enviar correo simple a {}: {}", to, e.getMessage());
            throw new NotificationException("Error al enviar correo simple a " + to, e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que es HTML

            mailSender.send(message);
            log.info("Correo HTML enviado exitosamente a: {}", to);
        } catch (MessagingException | MailException e) {
            log.error("Error al enviar correo HTML a {}: {}", to, e.getMessage());
            throw new NotificationException("Error al enviar correo HTML a " + to, e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token, String name) {
        String subject = "Restablecimiento de contraseña - Amazon Clone";

        Map<String, String> variables = Map.of(
                "name", name,
                "token", token);

        String htmlContent = templateLoader.loadTemplate("password-reset", variables);
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        String subject = "¡Bienvenido a Amazon Clone!";

        Map<String, String> variables = Map.of("name", name);

        String htmlContent = templateLoader.loadTemplate("welcome", variables);
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public void sendOrderConfirmationEmail(String to, String name, String orderNumber) {
        String subject = "Confirmación de pedido #" + orderNumber;

        Map<String, String> variables = Map.of(
                "name", name,
                "orderNumber", orderNumber);

        String htmlContent = templateLoader.loadTemplate("order-confirmation", variables);
        sendHtmlEmail(to, subject, htmlContent);
    }
}
