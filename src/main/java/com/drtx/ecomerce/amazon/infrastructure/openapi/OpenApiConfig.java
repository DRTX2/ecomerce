package com.drtx.ecomerce.amazon.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        Server localServer = new Server()
                .url("http://localhost:" + serverPort + contextPath)
                .description("Local Development Server");

        return new OpenAPI()
                .servers(List.of(localServer))
                .info(new Info()
                        .title("E-commerce Backend API")
                        .version("v1.0.0")
                        .description("""
                                API REST y GraphQL para plataforma de e-commerce construida con Arquitectura Hexagonal.

                                ## Módulos principales
                                - **Autenticación**: JWT con refresh tokens
                                - **Catálogo**: Productos, categorías, inventario
                                - **Carrito y Favoritos**: Persistentes por usuario
                                - **Órdenes**: Procesamiento completo con estados
                                - **Incidencias y Apelaciones**: Sistema híbrido REST + GraphQL
                                - **Notificaciones**: Email transaccionales (Azure Communication Services)

                                ## Documentación interactiva
                                - **Scalar UI**: `/scalar` (recomendado)
                                - **Swagger UI**: `/swagger-ui.html`
                                - **OpenAPI JSON**: `/v3/api-docs`
                                """)
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT (sin 'Bearer ' prefijo)")));
    }
}
