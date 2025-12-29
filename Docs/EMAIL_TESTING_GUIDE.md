# 📧 Guía para Probar el Envío de Correos

## 📋 Configuración Inicial

### 1. Configura tu archivo `.env`

Crea o edita el archivo `.env` en la raíz del proyecto `back/` con las siguientes variables:

```env
# Configuración de Outlook
MAIL_USERNAME=tu_correo@outlook.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion

# Correo donde recibirás los emails de prueba
TEST_EMAIL=correo_destino@gmail.com
```

### 2. Obtén una Contraseña de Aplicación de Outlook

**IMPORTANTE:** NO uses tu contraseña normal de Outlook.

#### Pasos para Outlook/Hotmail:

1. Ve a: https://account.microsoft.com/security
2. Haz clic en "Opciones de seguridad avanzadas"
3. Busca "Contraseñas de aplicación"
4. Crea una nueva contraseña de aplicación
5. Copia la contraseña generada (sin espacios)
6. Pégala en `MAIL_PASSWORD` en tu archivo `.env`

#### Pasos para Gmail (alternativa):

Si prefieres usar Gmail:

1. Cambia en `application.yml`:
   ```yaml
   spring:
     mail:
       host: smtp.gmail.com
       port: 587
   ```
2. Ve a: https://myaccount.google.com/apppasswords
3. Crea una contraseña de aplicación
4. Úsala en `MAIL_PASSWORD`

## 🧪 Ejecutar los Tests

**IMPORTANTE:** Los tests de envío de email usan el perfil `default` (producción) para cargar las credenciales reales de tu archivo `.env`. Asegúrate de tener el archivo `.env` configurado correctamente antes de ejecutar los tests.

### Opción 1: Desde tu IDE (IntelliJ IDEA / VS Code)

1. Abre el archivo:
   ```
   src/test/java/com/drtx/ecomerce/amazon/infrastructure/email/EmailRealSendTest.java
   ```

2. Encuentra el test que quieres ejecutar (ejemplo: `testSendSimpleEmail_Real`)

3. **IMPORTANTE:** Elimina o comenta la línea `@Disabled`:
   ```java
   @Test
   // @Disabled("Habilita este test solo cuando quieras enviar un correo real")
   void testSendSimpleEmail_Real() {
       // ...
   }
   ```

4. Haz clic derecho sobre el test → "Run"

5. Revisa la consola para ver el resultado

### Opción 2: Desde la Terminal

**IMPORTANTE:** Necesitas cargar las variables de entorno antes de ejecutar los tests.

#### En Linux/Mac:

```bash
# Navega a la carpeta back
cd back

# Carga las variables de entorno del archivo .env
export $(cat .env | grep -v '^#' | xargs)

# Ejecuta un test específico (primero elimina @Disabled del test)
./gradlew test --tests EmailRealSendTest.testSendSimpleEmail_Real

# O ejecuta todos los tests de email (solo los que no estén @Disabled)
./gradlew test --tests EmailRealSendTest
```

#### En Windows PowerShell:

```powershell
# Navega a la carpeta back
cd back

# Carga las variables de entorno
Get-Content .env | ForEach-Object {
    if ($_ -notmatch '^#' -and $_ -match '=') {
        $parts = $_ -split '=', 2
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), 'Process')
    }
}

# Ejecuta el test
.\gradlew.bat test --tests EmailRealSendTest.testSendSimpleEmail_Real
```

## 📬 Tests Disponibles

### 1. `testShowEmailConfiguration`
- ✅ Siempre habilitado
- Muestra la configuración actual sin enviar correos
- Útil para verificar que las variables estén cargadas correctamente

### 2. `testSendSimpleEmail_Real`
- 📧 Envía un correo de texto plano
- Útil para verificar la configuración básica

### 3. `testSendHtmlEmail_Real`
- 📧 Envía un correo HTML con formato y colores
- Verifica que el servidor puede enviar HTML

### 4. `testSendWelcomeEmail_Real`
- 📧 Envía el correo de bienvenida usando la plantilla
- Prueba la integración con el sistema de plantillas

### 5. `testSendPasswordResetEmail_Real`
- 📧 Envía el correo de reseteo de contraseña
- Prueba la plantilla de recuperación de contraseña

### 6. `testSendOrderConfirmationEmail_Real`
- 📧 Envía el correo de confirmación de pedido
- Prueba la plantilla de confirmación de compra

## ✅ Verificación

Después de ejecutar un test:

1. **Revisa la consola:**
   ```
   ═══════════════════════════════════════════════════════
   📧 ENVIANDO CORREO SIMPLE DE PRUEBA
   ═══════════════════════════════════════════════════════
   De:   tu_correo@outlook.com
   Para: correo_destino@gmail.com
   ═══════════════════════════════════════════════════════
   ✅ Correo enviado exitosamente!
   📬 Revisa tu bandeja de entrada en: correo_destino@gmail.com
   ⚠️  Si no lo ves, revisa la carpeta de SPAM
   ```

2. **Revisa tu bandeja de entrada** en el correo configurado en `TEST_EMAIL`

3. **Si no aparece:** Revisa la carpeta de SPAM/Correo no deseado

## 🔧 Solución de Problemas

### Error: "Authentication failed"

- Verifica que `MAIL_USERNAME` sea correcto
- Asegúrate de usar una **contraseña de aplicación**, no tu contraseña normal
- Verifica que la cuenta de Outlook tenga la autenticación de dos factores activada

### Error: "Could not connect to SMTP host"

- Verifica tu conexión a internet
- Comprueba que el puerto 587 no esté bloqueado por tu firewall
- Si usas una red corporativa, puede estar bloqueando SMTP

### Error: "Connection timeout"

- Aumenta el timeout en `application.yml`:
  ```yaml
  spring:
    mail:
      properties:
        mail:
          smtp:
            connectiontimeout: 10000
            timeout: 10000
            writetimeout: 10000
  ```

### Los correos llegan a SPAM

Esto es normal para servidores de prueba. Para producción:

1. Configura SPF records en tu dominio
2. Configura DKIM
3. Usa un servicio de email profesional (SendGrid, AWS SES, etc.)

## 📝 Notas Importantes

- ⚠️ Los tests están **deshabilitados por defecto** con `@Disabled` para evitar envíos accidentales
- 🔒 **NUNCA** subas tu archivo `.env` a GitHub
- 🔄 Después de probar, **vuelve a habilitar** `@Disabled` en los tests
- 📧 Los correos pueden tardar unos segundos en llegar

## 🎯 Uso en Desarrollo

Para ver solo la configuración sin enviar correos:

```bash
./gradlew test --tests EmailRealSendTest.testShowEmailConfiguration
```

Este test siempre está habilitado y muestra:
- Correo emisor configurado
- Correo de prueba configurado
- Instrucciones de uso

## 📚 Más Información

- Documentación de Spring Mail: https://docs.spring.io/spring-framework/reference/integration/email.html
- Configuración de Outlook: https://support.microsoft.com/smtp
- Contraseñas de aplicación de Microsoft: https://account.microsoft.com/security
