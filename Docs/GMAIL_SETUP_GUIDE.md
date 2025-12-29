# 📧 Guía Completa: Configurar Gmail para Enviar Correos desde Spring Boot

## 🎯 Objetivo

Configurar una cuenta de Gmail para que pueda enviar correos a través de SMTP desde tu aplicación Spring Boot.

## ⚠️ Requisito Previo

**IMPORTANTE**: Ya NO se puede usar la contraseña normal de Gmail. Debes crear una **Contraseña de Aplicación**.

## 📋 Pasos Detallados

### Paso 1: Verificación en Dos Pasos

1. Ve a [Google Account](https://myaccount.google.com/)
2. En el menú izquierdo, haz clic en **"Seguridad"**
3. Busca la sección **"Cómo inicias sesión en Google"**
4. Haz clic en **"Verificación en dos pasos"**
5. Si no está activada:
   - Haz clic en **"Comenzar"**
   - Sigue las instrucciones (necesitarás tu teléfono)
   - Completa la configuración

**✅ Resultado**: Ahora tienes la verificación en dos pasos activa.

---

### Paso 2: Crear Contraseña de Aplicación

1. Permanece en la página de **Seguridad**
2. Busca **"Contraseñas de aplicaciones"** (App passwords)
   - Si no la ves, intenta buscar directamente: [App Passwords](https://myaccount.google.com/apppasswords)
3. Haz clic en **"Contraseñas de aplicaciones"**
4. Te pedirá que ingreses tu contraseña de Gmail
5. Verás una pantalla para crear una nueva contraseña:

   **Opción 1** (Nueva interfaz):
   - En "Selecciona la app": Escribe un nombre, por ejemplo: `Amazon Backend`
   - Haz clic en **"Crear"**

   **Opción 2** (Interfaz antigua):
   - En "Selecciona la app": Elige **"Correo"**
   - En "Selecciona el dispositivo": Elige **"Otro (nombre personalizado)"**
   - Escribe: `Amazon Backend` o `Spring Boot App`
   - Haz clic en **"Generar"**

6. Google generará una contraseña de 16 caracteres como:
   ```
   abcd efgh ijkl mnop
   ```
7. **¡COPIA ESTA CONTRASEÑA!** (sin espacios)

**✅ Resultado**: Tienes una contraseña de aplicación lista para usar.

---

### Paso 3: Crear Archivo .env

1. En la raíz del proyecto backend, crea un archivo llamado `.env`:

```bash
cd /home/david/Desktop/personal/ecomerce-project/back
touch .env
```

2. Abre el archivo `.env` y agrega:

```bash
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=abcdefghijklmnop
```

**Reemplaza**:
- `tu_correo@gmail.com` con tu correo real
- `abcdefghijklmnop` con la contraseña que copiaste (SIN espacios)

**Ejemplo real**:
```bash
MAIL_USERNAME=david.developer@gmail.com
MAIL_PASSWORD=xyzw1234abcd5678
```

3. Guarda el archivo.

**✅ Resultado**: Tienes las credenciales guardadas de forma segura.

---

### Paso 4: Cargar Variables de Entorno

Dependiendo de tu entorno, elige una opción:

#### Opción A: Terminal (Linux/Mac)

```bash
export $(cat .env | xargs)
```

#### Opción B: PowerShell (Windows)

```powershell
Get-Content .env | ForEach-Object {
    $name, $value = $_.Split('=')
    Set-Item -Path "env:$name" -Value $value
}
```

#### Opción C: IntelliJ IDEA

1. Ve a: **Run** → **Edit Configurations**
2. Selecciona tu configuración de Spring Boot
3. En **Environment variables**, haz clic en el icono de carpeta
4. Haz clic en **"Load from file"** (icono de carpeta)
5. Selecciona tu archivo `.env`
6. Haz clic en **OK**

#### Opción D: VS Code

1. Instala la extensión **DotENV**
2. Crea un archivo `launch.json` en `.vscode/`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "request": "launch",
      "mainClass": "com.drtx.ecomerce.amazon.AmazonApplication",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

**✅ Resultado**: Las variables están cargadas en tu IDE.

---

### Paso 5: Verificar Configuración

1. Abre `application.yml` y verifica que tenga:

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
```

2. **NO modifiques** `application.yml` con credenciales reales.

**✅ Resultado**: La configuración está lista.

---

### Paso 6: Probar el Envío

#### Prueba 1: Ejecutar el Test de Integración

```bash
./gradlew test --tests "*EmailServiceIntegrationTest"
```

**Resultado esperado**:
```
BUILD SUCCESSFUL
```

#### Prueba 2: Crear un Endpoint de Prueba Temporal

**SOLO para desarrollo**, crea un controller temporal:

```java
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class EmailTestController {
    
    private final EmailService emailService;
    
    @GetMapping("/send-test-email")
    public ResponseEntity<String> sendTestEmail() {
        try {
            emailService.sendSimpleEmail(
                "TU_CORREO_DESTINO@gmail.com",  // Reemplaza con tu correo
                "Prueba desde Spring Boot",
                "¡Hola! Este correo fue enviado desde tu aplicación Spring Boot. Si lo recibiste, la configuración es correcta."
            );
            return ResponseEntity.ok("Correo enviado. Revisa tu bandeja de entrada.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error: " + e.getMessage());
        }
    }
}
```

**Ejecutar**:
1. Inicia la aplicación: `./gradlew bootRun`
2. Abre el navegador: `http://localhost:8080/api/v1/test/send-test-email`
3. Revisa tu correo

**⚠️ ELIMINA este controller después de probar.**

**✅ Resultado**: Si recibes el correo, ¡todo funciona!

---

## 🐛 Solución de Problemas

### Error: "Username and Password not accepted"

**Causa**: Estás usando la contraseña normal de Gmail.

**Solución**:
1. Verifica que estás usando la **contraseña de aplicación** (16 caracteres)
2. Cópiala nuevamente sin espacios
3. Actualiza `.env`

---

### Error: "Authentication failed"

**Causa**: Verificación en dos pasos no está activa.

**Solución**:
1. Ve a [Seguridad de Google](https://myaccount.google.com/security)
2. Activa la verificación en dos pasos
3. Crea una nueva contraseña de aplicación

---

### Error: "Could not connect to SMTP server"

**Causa 1**: Firewall bloqueando puerto 587.

**Solución**:
```bash
# Verificar si el puerto está abierto
telnet smtp.gmail.com 587
```

**Causa 2**: Sin conexión a internet.

**Solución**: Verifica tu conexión.

---

### Error: "Invalid Addresses"

**Causa**: Correo del destinatario mal formado.

**Solución**:
- Verifica que el correo destino sea válido
- Asegúrate de que no tenga espacios

---

### Las variables de entorno no se cargan

**Causa**: No exportaste las variables antes de iniciar.

**Solución**:
1. Cierra la aplicación
2. Exporta las variables nuevamente
3. Reinicia la aplicación

---

## 📊 Límites de Gmail SMTP

Gmail tiene límites de envío:

| Tipo de Cuenta | Límite Diario |
|----------------|---------------|
| Gmail Gratuito | 500 correos/día |
| Google Workspace | 2000 correos/día |

Si necesitas más, considera:
- **SendGrid**: 100 correos/día gratis
- **AWS SES**: $0.10 por 1000 correos
- **Mailgun**: 5000 correos/mes gratis

---

## 🔒 Seguridad

### ✅ Buenas Prácticas

1. **NUNCA** commitees el archivo `.env`
   - Está en `.gitignore`
   - Verifica con: `git status`

2. **NUNCA** pongas credenciales en `application.yml`

3. **Rotación de contraseñas**:
   - Cambia las contraseñas de aplicación cada 3-6 meses
   - Elimina las que no uses

4. **Para producción**:
   - Usa servicios de secretos (AWS Secrets Manager, Azure Key Vault)
   - O variables de entorno del servidor

### ❌ NO Hagas Esto

```yaml
# ❌ MAL - NO hagas esto
spring:
  mail:
    username: mi_correo@gmail.com
    password: abcd1234efgh5678
```

```java
// ❌ MAL - NO hagas esto
emailService.sendEmail("destino@gmail.com", "Asunto", "Mensaje");
```

---

## ✅ Checklist Final

Antes de dar por terminada la configuración:

- [ ] Verificación en dos pasos activa en Google
- [ ] Contraseña de aplicación generada
- [ ] Archivo `.env` creado con credenciales
- [ ] `.env` está en `.gitignore`
- [ ] Variables cargadas en el entorno
- [ ] Test de integración pasa: `./gradlew test --tests "*EmailServiceIntegrationTest"`
- [ ] Correo de prueba recibido correctamente
- [ ] Controller de prueba eliminado (si lo creaste)

---

## 📚 Referencias

- [Google App Passwords](https://support.google.com/accounts/answer/185833)
- [Gmail SMTP Settings](https://support.google.com/mail/answer/7126229)
- [Spring Boot Mail](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.email)

---

## 🎉 ¡Listo!

Si completaste todos los pasos y el checklist, tu aplicación puede enviar correos. 

**Próximos pasos**:
- Lee [EMAIL_PASSWORD_RESET_INTEGRATION.md](./EMAIL_PASSWORD_RESET_INTEGRATION.md) para implementar restablecimiento de contraseña
- Lee [EMAIL_AUTH_SERVICE_INTEGRATION_EXAMPLE.md](./EMAIL_AUTH_SERVICE_INTEGRATION_EXAMPLE.md) para enviar correos de bienvenida

---

**Creado por**: GitHub Copilot  
**Fecha**: 27 de Diciembre de 2024
