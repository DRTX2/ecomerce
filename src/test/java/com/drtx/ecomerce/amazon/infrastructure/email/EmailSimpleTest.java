package com.drtx.ecomerce.amazon.infrastructure.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Test ULTRA SIMPLE para probar envío de correos SIN Spring Boot
 * Esto nos ayuda a identificar si el problema es de configuración o de credenciales
 */
public class EmailSimpleTest {

    @Test
//    @Disabled("Elimina @Disabled para ejecutar")
    void testEmailConOutlook() {
        // Configuración directa desde tu .env
        String username = "dave-man5432@outlook.com";
        String password = "fufuxrditzrijkuq";  // Tu contraseña del .env
        String destinatario = "davidmanjarres2004@gmail.com";

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("🧪 TEST SIMPLE DE EMAIL - OUTLOOK");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Usuario:      " + username);
        System.out.println("Contraseña:   " + password.substring(0, 4) + "***");
        System.out.println("Destinatario: " + destinatario);
        System.out.println("═══════════════════════════════════════════\n");

        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.office365.com");
            mailSender.setPort(587);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.office365.com");
            props.put("mail.debug", "true");  // Activar debug para ver detalles

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(destinatario);
            message.setSubject("✅ Test Simple - Sin Spring Boot");
            message.setText("Si ves este mensaje, la configuración funciona!");

            System.out.println("📧 Intentando enviar correo...");
            mailSender.send(message);
            System.out.println("✅ ¡CORREO ENVIADO EXITOSAMENTE!");
            System.out.println("📬 Revisa tu bandeja: " + destinatario);

        } catch (Exception e) {
            System.err.println("❌ ERROR AL ENVIAR:");
            System.err.println("Tipo: " + e.getClass().getSimpleName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n💡 POSIBLES SOLUCIONES:");
            System.out.println("1. ⚠️  La contraseña NO es válida para Outlook");
            System.out.println("   → Genera una nueva en: https://account.microsoft.com/security");
            System.out.println("   → Ve a 'Contraseñas de aplicación' y crea una nueva");
            System.out.println("\n2. 🔒 Outlook puede requerir configuración adicional");
            System.out.println("   → Verifica que la verificación en 2 pasos esté activa");
            System.out.println("\n3. 📧 RECOMENDACIÓN: USA GMAIL en su lugar");
            System.out.println("   → Es más simple y confiable para desarrollo");
            System.out.println("   → Cambia MAIL_USERNAME a tu Gmail");
            System.out.println("   → Genera contraseña en: https://myaccount.google.com/apppasswords");
            
            throw new RuntimeException("Test falló. Lee las soluciones arriba.", e);
        }
    }

    @Test
//    @Disabled("Elimina @Disabled para ejecutar")
    void testEmailConGmail() {
        // ALTERNATIVA CON GMAIL - MÁS FÁCIL
        String username = "davidmanjarres2004@gmail.com";  // TU GMAIL
        String password = "PON_AQUI_CONTRASEÑA_DE_APLICACION";  // Genera en https://myaccount.google.com/apppasswords
        String destinatario = "dave-man5432@outlook.com";

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("🧪 TEST SIMPLE DE EMAIL - GMAIL");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Usuario:      " + username);
        System.out.println("Destinatario: " + destinatario);
        System.out.println("═══════════════════════════════════════════\n");

        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.debug", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(destinatario);
            message.setSubject("✅ Test Gmail - Sin Spring Boot");
            message.setText("Gmail funciona perfectamente!");

            System.out.println("📧 Intentando enviar correo desde Gmail...");
            mailSender.send(message);
            System.out.println("✅ ¡CORREO ENVIADO EXITOSAMENTE CON GMAIL!");
            System.out.println("📬 Revisa tu bandeja: " + destinatario);

        } catch (Exception e) {
            System.err.println("❌ ERROR AL ENVIAR:");
            e.printStackTrace();
            throw new RuntimeException("Test falló", e);
        }
    }
}
