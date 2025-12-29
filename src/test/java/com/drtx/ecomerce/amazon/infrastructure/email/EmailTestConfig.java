package com.drtx.ecomerce.amazon.infrastructure.email;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

/**
 * Configuración de test para el servicio de correo electrónico.
 * 
 * Proporciona un mock de JavaMailSender para evitar enviar correos reales
 * durante la ejecución de los tests.
 */
@TestConfiguration
public class EmailTestConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
}
