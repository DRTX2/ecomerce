package com.drtx.ecomerce.amazon.infrastructure.email;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.drtx.ecomerce.amazon.core.model.exceptions.NotificationException;
import com.drtx.ecomerce.amazon.core.ports.out.notification.EmailPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Adaptador para envío de correos electrónicos utilizando Microsoft Azure
 * Communication Services.
 * Se activa cuando la propiedad 'email.provider' es 'azure'.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "email.provider", havingValue = "azure")
public class AzureEmailAdapter implements EmailPort {

    private final EmailTemplateLoader templateLoader;

    @Value("${azure.communication.connection-string}")
    private String connectionString;

    @Value("${azure.communication.sender-address}")
    private String senderAddress;

    private EmailClient emailClient;

    @PostConstruct
    public void init() {
        try {
            this.emailClient = new EmailClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            log.info("Azure EmailClient initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Azure EmailClient", e);
            throw new RuntimeException("Failed to initialize Azure EmailClient", e);
        }
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            EmailAddress toAddress = new EmailAddress(to);
            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(senderAddress)
                    .setToRecipients(toAddress)
                    .setSubject(subject)
                    .setBodyPlainText(text);

            sendEmail(emailMessage, to);
            log.info("Azure Simple Email sent to {}", to);
        } catch (Exception e) {
            log.error("Error sending Azure simple email to {}", to, e);
            throw new NotificationException("Error sending Azure simple email to " + to, e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            EmailAddress toAddress = new EmailAddress(to);
            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(senderAddress)
                    .setToRecipients(toAddress)
                    .setSubject(subject)
                    .setBodyHtml(htmlContent);

            sendEmail(emailMessage, to);
            log.info("Azure HTML Email sent to {}", to);
        } catch (Exception e) {
            log.error("Error sending Azure HTML email to {}", to, e);
            throw new NotificationException("Error sending Azure HTML email to " + to, e);
        }
    }

    private void sendEmail(EmailMessage emailMessage, String to) {
        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> response = poller.waitForCompletion();

        if (response.getValue().getStatus().toString().equals("Succeeded")) {
            log.debug("Email send operation status: {}", response.getStatus());
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
