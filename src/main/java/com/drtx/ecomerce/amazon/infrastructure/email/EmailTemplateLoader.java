package com.drtx.ecomerce.amazon.infrastructure.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * Servicio para cargar y procesar plantillas HTML de correos electrónicos.
 */
@Component
@Slf4j
public class EmailTemplateLoader {

    private static final String TEMPLATE_PATH = "templates/email/";

    /**
     * Carga una plantilla HTML y reemplaza las variables con los valores
     * proporcionados.
     *
     * @param templateName Nombre del archivo de plantilla (sin extensión)
     * @param variables    Mapa de variables a reemplazar en la plantilla
     * @return Contenido HTML procesado
     */
    public String loadTemplate(String templateName, Map<String, String> variables) {
        try {
            String template = loadTemplateFile(templateName);
            return processTemplate(template, variables);
        } catch (IOException e) {
            log.error("Error al cargar plantilla {}: {}", templateName, e.getMessage());
            throw new RuntimeException("Error al cargar plantilla de email: " + templateName, e);
        }
    }

    /**
     * Carga el contenido de un archivo de plantilla desde resources.
     */
    private String loadTemplateFile(String templateName) throws IOException {
        String path = TEMPLATE_PATH + templateName + ".html";
        ClassPathResource resource = new ClassPathResource(path);

        if (!resource.exists()) {
            throw new IOException("Plantilla no encontrada: " + path);
        }

        return Files.readString(
                resource.getFile().toPath(),
                StandardCharsets.UTF_8);
    }

    /**
     * Procesa la plantilla reemplazando las variables con formato {{variable}}.
     */
    private String processTemplate(String template, Map<String, String> variables) {
        String result = template;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue());
        }

        return result;
    }
}
