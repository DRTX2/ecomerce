# Módulo de Envío de Correos Electrónicos

## 📧 Descripción

Este módulo proporciona funcionalidad para enviar correos electrónicos a través de SMTP usando Spring Boot Mail, siguiendo la arquitectura hexagonal.
Implementa el puerto `EmailPort` a través del adaptador `EmailAdapter`.

## 🏗️ Arquitectura

El módulo sigue una arquitectura limpia con separación de responsabilidades:

- **`EmailPort`** (Core): Interface que define las operaciones de envío de correos
- **`EmailAdapter`** (Infrastructure): Implementación del puerto usando JavaMailSender
- **`EmailTemplateLoader`** (Infrastructure): Carga y procesa plantillas HTML desde archivos
- **Templates HTML** (`src/main/resources/templates/email/`): Plantillas separadas del código Java

## 🔧 Configuración

### 1. Variables de Entorno Requeridas

Debes configurar las siguientes variables de entorno antes de ejecutar la aplicación:

```bash
export MAIL_USERNAME="tu_correo@gmail.com"
export MAIL_PASSWORD="tu_contraseña_de_aplicación"
```

### 2. Obtener Contraseña de Aplicación de Gmail

⚠️ **IMPORTANTE**: Gmail ya NO permite usar la contraseña normal de la cuenta.

**Pasos para crear una Contraseña de Aplicación:**

1. Ve a tu [Cuenta de Google](https://myaccount.google.com/)
2. Entra a **Seguridad**
3. Activa **Verificación en dos pasos** (si no la tienes activada)
4. Busca **Contraseñas de aplicaciones**
5. Selecciona:
   - **App:** Mail
   - **Dispositivo:** Otro (o el nombre de tu aplicación)
6. Google generará una contraseña como: `abcd efgh ijkl mnop`
7. **Copia esa contraseña** y úsala en `MAIL_PASSWORD`

### 3. Configuración en application.yml

La configuración SMTP ya está lista en `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:tu_correo@gmail.com}
    password: ${MAIL_PASSWORD:CONTRASEÑA_DE_APLICACIÓN}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

## 📝 Uso del Servicio

### Inyectar el Servicio

```java
@Service
public class AuthService {
    
    private final EmailPort emailPort;
    
    public AuthService(EmailPort emailPort) {
        this.emailPort = emailPort;
    }
}
```

### Métodos Disponibles

#### 1. Enviar Correo Simple (Texto Plano)

```java
emailPort.sendSimpleEmail(
    "destinatario@example.com",
    "Asunto del correo",
    "Contenido del mensaje en texto plano"
);
```

#### 2. Enviar Correo HTML

```java
String htmlContent = """
    <h1>¡Hola!</h1>
    <p>Este es un correo con formato HTML</p>
    """;

emailPort.sendHtmlEmail(
    "destinatario@example.com",
    "Asunto del correo",
    htmlContent
);
```

#### 3. Enviar Correo de Restablecimiento de Contraseña

```java
emailPort.sendPasswordResetEmail(
    "usuario@example.com",
    "ABC123XYZ",  // Token de restablecimiento
    "Juan"        // Nombre del usuario
);
```

Este método envía un correo HTML profesional con:
- El token de restablecimiento destacado
- Instrucciones claras
- Advertencia de seguridad
- Expiración del token

#### 4. Enviar Correo de Bienvenida

```java
emailPort.sendWelcomeEmail(
    "nuevousuario@example.com",
    "María"  // Nombre del usuario
);
```

Incluye:
- Mensaje de bienvenida
- Lista de características de la plataforma
- Diseño profesional

#### 5. Enviar Confirmación de Pedido

```java
emailPort.sendOrderConfirmationEmail(
    "cliente@example.com",
    "Carlos",           // Nombre del cliente
    "ORD-2024-12345"    // Número de pedido
);
```

Incluye:
- Confirmación del pedido
- Número de pedido destacado
- Mensaje de seguimiento

## 🧪 Tests

Los tests de integración están en `EmailAdapterIntegrationTest.java`:

```bash
./gradlew test --tests "EmailAdapterIntegrationTest"
```

Los tests usan un `JavaMailSender` mockeado para **NO enviar correos reales** durante las pruebas.

## 🎨 Plantillas HTML

Las plantillas HTML están separadas del código Java en archivos individuales:

```
src/main/resources/templates/email/
├── password-reset.html
├── welcome.html
└── order-confirmation.html
```

### Sistema de Variables

Las plantillas usan un sistema simple de reemplazo de variables con el formato `{{variable}}`:

```html
<h2>Hola {{name}},</h2>
<div class="token-box">{{token}}</div>
```

### Añadir una Nueva Plantilla

1. Crea un archivo HTML en `src/main/resources/templates/email/`
2. Usa `{{variableName}}` para las variables dinámicas
3. Carga la plantilla usando `EmailTemplateLoader`:

```java
Map<String, String> variables = Map.of(
    "name", userName,
    "customField", value
);
String html = templateLoader.loadTemplate("mi-plantilla", variables);
```

### Estilos CSS Incluidos

Las plantillas usan CSS inline con:
- Colores del tema Amazon (`#232f3e`, `#ff9900`)
- Diseño responsive
- Tipografía profesional

## 🔒 Seguridad

⚠️ **NUNCA subas las credenciales a GitHub**

Para desarrollo local:
```bash
# .env (no commitear este archivo)
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

Para producción, usa variables de entorno del servidor o servicios como:
- AWS Secrets Manager
- Azure Key Vault
- HashiCorp Vault

## 🚀 Proveedores SMTP Alternativos

Además de Gmail, puedes usar:

### Outlook/Hotmail
```yaml
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
```

### Yahoo
```yaml
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
```

### SendGrid (servicio profesional)
```yaml
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=TU_API_KEY
```

## 📊 Límites de Gmail

Gmail SMTP gratuito tiene límites:
- **500 correos/día** para cuentas gratuitas
- **2000 correos/día** para cuentas de Google Workspace

Para mayor volumen, considera servicios como SendGrid, AWS SES, o Mailgun.

## 🐛 Troubleshooting

### Error: "Authentication failed"
- Verifica que estás usando una **Contraseña de Aplicación**, no la contraseña normal
- Asegúrate de tener activada la verificación en dos pasos

### Error: "Could not connect to SMTP server"
- Verifica que el puerto 587 no esté bloqueado por tu firewall
- Confirma que tienes conexión a internet

### Error: "Recipient address rejected"
- Verifica que el correo del destinatario sea válido
- Algunos proveedores requieren verificar el dominio

## 📚 Referencias

- [Spring Boot Mail Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.email)
- [JavaMail API](https://jakarta.ee/specifications/mail/)
- [Gmail SMTP Settings](https://support.google.com/mail/answer/7126229)
