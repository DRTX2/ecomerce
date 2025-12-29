#!/bin/bash

# Script para ejecutar los tests de email cargando las variables del .env
# Uso: ./run-email-test.sh [nombre_del_test]
# Ejemplo: ./run-email-test.sh testSendSimpleEmail_Real

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}📧 Test de Envío de Emails - Amazon Clone${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"

# Verificar que existe el archivo .env
if [ ! -f ".env" ]; then
    echo -e "${RED}❌ Error: No se encontró el archivo .env${NC}"
    echo -e "${YELLOW}Por favor, crea el archivo .env con tus credenciales de email${NC}"
    echo -e "${YELLOW}Puedes usar .env.example como referencia${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Archivo .env encontrado${NC}"

Cargar variables de entorno
echo -e "${YELLOW}📋 Cargando variables de entorno...${NC}"
export $(cat .env | grep -v '^#' | xargs)

# Verificar que las variables críticas estén configuradas
if [ -n "$AZURE_COMMUNICATION_CONNECTION_STRING" ]; then
    echo -e "${GREEN}✅ Configuración de Azure detectada${NC}"
    PROVIDER="azure"
elif [ -n "$MAIL_USERNAME" ] && [ "$MAIL_USERNAME" != "tu_correo@gmail.com" ]; then
    echo -e "${GREEN}✅ Configuración de JavaMail (SMTP) detectada${NC}"
    PROVIDER="javamail"
    
    if [ -z "$MAIL_PASSWORD" ] || [ "$MAIL_PASSWORD" = "abcd efgh ijkl mnop" ]; then
        echo -e "${RED}❌ Error: MAIL_PASSWORD no está configurado correctamente${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ Error: No se detectaron credenciales válidas (Azure o SMTP)${NC}"
    echo -e "${YELLOW}Configura AZURE_COMMUNICATION_CONNECTION_STRING o MAIL_USERNAME/MAIL_PASSWORD en .env${NC}"
    exit 1
fi

if [ -z "$TEST_EMAIL" ] || [ "$TEST_EMAIL" = "correo_prueba@example.com" ]; then
    if [ "$PROVIDER" = "javamail" ]; then
        echo -e "${YELLOW}⚠️  Advertencia: TEST_EMAIL no está configurado${NC}"
        echo -e "${YELLOW}Se usará MAIL_USERNAME como destinatario de prueba${NC}"
        export TEST_EMAIL=$MAIL_USERNAME
    else
        echo -e "${RED}❌ Error: TEST_EMAIL es requerido para pruebas con Azure${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✅ Configuración de prueba:${NC}"
echo -e "   - Proveedor: $PROVIDER"
echo -e "   - Destinatario: $TEST_EMAIL"
if [ "$PROVIDER" = "azure" ]; then
    echo -e "   - Sender Agent: Detectado de variable AZURE_SENDER_ADDRESS"
else
    echo -e "   - Sender: $MAIL_USERNAME"
fi
echo -e ""

# Determinar qué test ejecutar
if [ -z "$1" ]; then
    echo -e "${YELLOW}📝 Ejecutando todos los tests de email no deshabilitados...${NC}"
    TEST_CLASS="EmailRealSendTest"
else
    echo -e "${YELLOW}📝 Ejecutando test: $1${NC}"
    TEST_CLASS="EmailRealSendTest.$1"
fi

echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e ""

# Ejecutar el test
./gradlew test --tests "$TEST_CLASS"

TEST_RESULT=$?

echo -e ""
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"

if [ $TEST_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ Test completado exitosamente!${NC}"
    echo -e "${GREEN}📬 Revisa tu bandeja de entrada en: $TEST_EMAIL${NC}"
    echo -e "${YELLOW}⚠️  Si no ves el correo, revisa la carpeta de SPAM${NC}"
else
    echo -e "${RED}❌ El test falló. Revisa los logs arriba para más detalles.${NC}"
    echo -e ""
    echo -e "${YELLOW}Posibles causas:${NC}"
    echo -e "   - Credenciales incorrectas (verifica MAIL_USERNAME y MAIL_PASSWORD)"
    echo -e "   - Contraseña de aplicación inválida"
    echo -e "   - Problemas de conexión a internet"
    echo -e "   - Puerto SMTP bloqueado por firewall"
fi

echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
