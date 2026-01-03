#!/bin/bash

# ============================================================================
# migrate.sh - Script de utilidad para gestionar migraciones con Flyway
# ============================================================================
# Uso: ./migrate.sh [comando]
# Comandos:
#   status    - Ver estado de migraciones
#   migrate   - Ejecutar migraciones pendientes
#   info      - Mostrar información detallada
#   clean     - LIMPIAR BD (solo desarrollo, borra TODO)
#   validate  - Validar integridad de migraciones
# ============================================================================

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

function print_header() {
    echo -e "${BLUE}============================================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================================================${NC}"
}

function print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

function print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

function print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Cambiar al directorio del proyecto
cd "$PROJECT_ROOT" || exit 1

case "${1:-status}" in
    status)
        print_header "Estado de Migraciones Flyway"
        ./gradlew flywayInfo --info 2>/dev/null || {
            print_error "No se pudo obtener información de Flyway"
            echo "Asegúrate que:"
            echo "1. La BD está corriendo"
            echo "2. Las credenciales en .env son correctas"
            echo "3. Has ejecutado: ./gradlew build"
            exit 1
        }
        ;;
    
    migrate)
        print_header "Ejecutando Migraciones Pendientes"
        if ./gradlew flywayMigrate --info; then
            print_success "Migraciones completadas exitosamente"
        else
            print_error "Error al ejecutar migraciones"
            exit 1
        fi
        ;;
    
    info)
        print_header "Información Detallada de Migraciones"
        ./gradlew flywayInfo --info
        ;;
    
    clean)
        print_warning "OPERACIÓN DESTRUCTIVA: Se eliminará TODA la base de datos"
        echo "Esta operación solo debe hacerse en DESARROLLO"
        read -p "¿Estás seguro? (escribe 'sí' para confirmar): " -r
        if [[ $REPLY == "sí" ]]; then
            print_header "Limpiando Base de Datos"
            if ./gradlew flywayClean --info; then
                print_success "Base de datos limpiada"
                print_warning "Deberás ejecutar migraciones de nuevo: ./migrate.sh migrate"
            else
                print_error "Error al limpiar la BD"
                exit 1
            fi
        else
            print_warning "Operación cancelada"
        fi
        ;;
    
    validate)
        print_header "Validando Migraciones"
        if ./gradlew flywayValidate --info; then
            print_success "Todas las migraciones son válidas"
        else
            print_error "Hay errores en las migraciones"
            echo ""
            echo "Posibles causas:"
            echo "1. Modificación de migración ya ejecutada"
            echo "2. Archivos duplicados"
            echo "3. Checksum incorrecto"
            exit 1
        fi
        ;;
    
    help|--help|-h)
        echo "Uso: $0 [comando]"
        echo ""
        echo "Comandos disponibles:"
        echo "  status      Ver estado de migraciones (default)"
        echo "  migrate     Ejecutar migraciones pendientes"
        echo "  info        Mostrar información detallada"
        echo "  validate    Validar integridad de migraciones"
        echo "  clean       LIMPIAR BD completa (solo desarrollo)"
        echo "  help        Mostrar esta ayuda"
        echo ""
        echo "Ejemplos:"
        echo "  $0 status"
        echo "  $0 migrate"
        echo "  $0 validate"
        ;;
    
    *)
        print_error "Comando desconocido: $1"
        echo "Ejecuta '$0 help' para ver comandos disponibles"
        exit 1
        ;;
esac
