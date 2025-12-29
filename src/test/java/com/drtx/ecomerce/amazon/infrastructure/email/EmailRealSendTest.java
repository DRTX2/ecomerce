package com.drtx.ecomerce.amazon.infrastructure.email;

import com.drtx.ecomerce.amazon.core.ports.out.notification.EmailPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "file:.env")
class EmailRealSendTest {

    @Autowired(required = false)
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @Autowired
    private EmailPort emailPort;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Value("${TEST_EMAIL:${email.test.recipient:test@example.com}}")
    private String testRecipient;

    @Value("${email.provider:javamail}")
    private String emailProvider;

    private String getFromEmail() {
        if ("azure".equalsIgnoreCase(emailProvider)) {
            return environment.getProperty("azure.communication.sender-address");
        }
        return environment.getProperty("spring.mail.username");
    }

    @Test
    void testSendSimpleEmail_Real() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("📧 ENVIANDO CORREO SIMPLE DE PRUEBA");
        System.out.println("═══════════════════════════════════════════════════════");
        String from = getFromEmail();
        System.out.println("De:   " + from);
        System.out.println("Para: " + testRecipient);
        System.out.println("═══════════════════════════════════════════════════════");

        String subject = "✅ Test de Correo Simple - Amazon Clone (" + emailProvider + ")";
        String text = """
                Hola,

                Este es un correo de prueba enviado desde el sistema Amazon Clone usando %s.

                Si recibes este mensaje, significa que la configuración de correo
                funciona correctamente.

                Fecha: %s
                """.formatted(emailProvider, java.time.LocalDateTime.now());

        assertDoesNotThrow(() -> {
            emailPort.sendSimpleEmail(testRecipient, subject, text);
            System.out.println("✅ Correo enviado exitosamente!");
        });
    }

    @Test
    void testSendHtmlEmail_Real() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("📧 ENVIANDO CORREO HTML DE PRUEBA");
        System.out.println("═══════════════════════════════════════════════════════");
        String from = getFromEmail();
        System.out.println("De:   " + from);
        System.out.println("Para: " + testRecipient);

        String subject = "✅ Test de Correo HTML - Amazon Clone (" + emailProvider + ")";
        String htmlContent = """
                <h1>Test HTML Exitoso</h1>
                <p>Proveedor: <strong>%s</strong></p>
                <p>Fecha: %s</p>
                """.formatted(emailProvider, java.time.LocalDateTime.now());

        assertDoesNotThrow(() -> {
            emailPort.sendHtmlEmail(testRecipient, subject, htmlContent);
            System.out.println("✅ Correo HTML enviado exitosamente!");
        });
    }

    @Test
    void testSendWelcomeEmail_Real() {
        System.out.println("📧 ENVIANDO CORREO DE BIENVENIDA DE PRUEBA");
        assertDoesNotThrow(() -> {
            emailPort.sendWelcomeEmail(testRecipient, "Usuario de Prueba");
            System.out.println("✅ Correo de bienvenida enviado exitosamente!");
        });
    }

    @Test
    void testSendPasswordResetEmail_Real() {
        System.out.println("📧 ENVIANDO CORREO DE RESETEO DE CONTRASEÑA DE PRUEBA");
        assertDoesNotThrow(() -> {
            emailPort.sendPasswordResetEmail(testRecipient, "TOKEN-123", "Usuario de Prueba");
            System.out.println("✅ Correo de reseteo enviado exitosamente!");
        });
    }

    @Test
    void testSendOrderConfirmationEmail_Real() {
        System.out.println("📧 ENVIANDO CORREO DE CONFIRMACIÓN DE PEDIDO");
        assertDoesNotThrow(() -> {
            emailPort.sendOrderConfirmationEmail(testRecipient, "Usuario de Prueba",
                    "ORD-" + System.currentTimeMillis());
            System.out.println("✅ Correo de orden enviado exitosamente!");
        });
    }

    @Test
    void testShowEmailConfiguration() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("📋 DIAGNÓSTICO COMPLETO DE CONFIGURACIÓN DE EMAIL");
        System.out.println("═══════════════════════════════════════════════════════");

        System.out.println("\n🔧 Configuración General:");
        System.out.println("   Proveedor activo:            " + emailProvider);
        System.out.println("   Correo de prueba (TEST_EMAIL): " + testRecipient);
        System.out.println("   Correo emisor (fromEmail):    " + getFromEmail());

        if ("azure".equalsIgnoreCase(emailProvider)) {
            System.out.println("\n☁️ Configuración Azure:");
            String connString = environment.getProperty("azure.communication.connection-string");
            System.out.println("   Connection String: " +
                    (connString != null && !connString.isEmpty()
                            ? "***configurada***"
                            : "❌ NO CONFIGURADA ❌"));
            System.out.println("   Sender Address:    " +
                    environment.getProperty("azure.communication.sender-address"));
        } else {
            try {
                if (mailSender instanceof org.springframework.mail.javamail.JavaMailSenderImpl senderImpl) {
                    var props = senderImpl.getJavaMailProperties();
                    System.out.println("\n📧 Propiedades SMTP:");
                    System.out.println("   Host:      " + senderImpl.getHost());
                    System.out.println("   Port:      " + senderImpl.getPort());
                    System.out.println("   Auth:      " + props.getProperty("mail.smtp.auth"));
                    System.out.println("   STARTTLS:  " + props.getProperty("mail.smtp.starttls.enable"));
                    System.out.println("   Username:  " + senderImpl.getUsername());
                    System.out.println("   Password:  " +
                            (senderImpl.getPassword() != null && !senderImpl.getPassword().isEmpty()
                                    ? "***configurada***"
                                    : "❌ NO CONFIGURADA ❌"));
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error leyendo configuración SMTP: " + e.getMessage());
            }
        }

        System.out.println("\n🌍 Variables de entorno:");
        if ("azure".equalsIgnoreCase(emailProvider)) {
            System.out.println("   AZURE_COMMUNICATION_CONNECTION_STRING: " +
                    (System.getenv("AZURE_COMMUNICATION_CONNECTION_STRING") != null ? "***presente***" : "❌ AUSENTE"));
            System.out.println("   AZURE_SENDER_ADDRESS: " + System.getenv("AZURE_SENDER_ADDRESS"));
        } else {
            System.out.println("   MAIL_USERNAME: " + System.getenv("MAIL_USERNAME"));
            System.out.println("   MAIL_PASSWORD: " +
                    (System.getenv("MAIL_PASSWORD") != null ? "***configurada***" : "❌ NO CONFIGURADA ❌"));
        }
        System.out.println("   TEST_EMAIL: " + System.getenv("TEST_EMAIL"));

        System.out.println("\n💡 CÓMO EJECUTAR LOS TESTS DE ENVÍO:");
        System.out.println("1. Configura tu archivo .env");
        System.out.println("2. Elimina @Disabled del test real");
        System.out.println("3. Ejecuta desde tu IDE");
        System.out.println("4. Revisa inbox y spam");
        System.out.println("═══════════════════════════════════════════════════════\n");

        assertNotNull(getFromEmail());
        assertNotNull(testRecipient);
        assertDoesNotThrow(() -> emailPort);
    }
}
