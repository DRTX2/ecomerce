# 🛠️ COMANDOS ÚTILES Y GUÍA DE DESARROLLO

---

## 🏗️ COMPILACIÓN Y BUILD

### Compilar el proyecto
```bash
cd /home/david/Desktop/personal/ecomerce-project/back
./gradlew build -x test
```

### Solo compilar código Java (rápido)
```bash
./gradlew compileJava
```

### Compilar incluyendo tests
```bash
./gradlew build
```

### Limpiar y construir desde cero
```bash
./gradlew clean build -x test
```

### Ver dependencias del proyecto
```bash
./gradlew dependencies
```

---

## 🧪 TESTING

### Ejecutar todos los tests
```bash
./gradlew test
```

### Ejecutar tests de un módulo específico
```bash
./gradlew test --tests "*CartControllerTest*"
```

### Ejecutar tests con output detallado
```bash
./gradlew test --info
```

### Generar reporte de tests
```bash
./gradlew test
# El reporte estará en: build/reports/tests/test/index.html
```

---

## 🚀 EJECUCIÓN

### Ejecutar la aplicación
```bash
./gradlew bootRun
```

### Ejecutar con propiedades personalizadas
```bash
./gradlew bootRun --args='--server.port=8081'
```

### Ver logs detallados
```bash
./gradlew bootRun --info
```

---

## 📝 BÚSQUEDA Y NAVEGACIÓN

### Buscar una clase
```bash
find /home/david/Desktop/personal/ecomerce-project/back/src -name "CartController.java"
```

### Buscar en todo el código
```bash
grep -r "CartUseCasePort" /home/david/Desktop/personal/ecomerce-project/back/src
```

### Contar líneas de código
```bash
find /home/david/Desktop/personal/ecomerce-project/back/src -name "*.java" | xargs wc -l | tail -1
```

### Listar todos los controladores
```bash
find /home/david/Desktop/personal/ecomerce-project/back/src -name "*Controller.java" | sort
```

### Listar todos los use cases
```bash
find /home/david/Desktop/personal/ecomerce-project/back/src -name "*UseCaseImpl.java" | sort
```

### Listar todas las entidades
```bash
find /home/david/Desktop/personal/ecomerce-project/back/src -path "*/model/*" -name "*.java" | sort
```

---

## 🔍 ANÁLISIS DE ERRORES

### Ver errores de compilación
```bash
./gradlew compileJava 2>&1 | grep -A 5 "error:"
```

### Ver warnings
```bash
./gradlew compileJava 2>&1 | grep -i "warning"
```

### Ejecutar análisis de código estático
```bash
./gradlew check
```

---

## 📦 GESTIÓN DE DEPENDENCIAS

### Ver árbol de dependencias
```bash
./gradlew dependencyTree
```

### Buscar conflictos de versión
```bash
./gradlew dependencyInsight --dependency=org.springframework
```

### Actualizar dependencias
```bash
# Editar build.gradle y luego:
./gradlew clean build
```

---

## 📝 VERIFICACIONES PRE-COMMIT

### Checklist antes de hacer commit
```bash
# 1. Compilar
./gradlew build -x test

# 2. Ejecutar tests
./gradlew test

# 3. Ver cambios
git status

# 4. Revisar el diff
git diff src/

# 5. Agregar cambios
git add .

# 6. Commit
git commit -m "Descripción del cambio"
```

---

## 🎯 ACCIONES COMUNES POR TAREA

### Crear una nueva clase en el dominio
```bash
# 1. Crear archivo
touch /home/david/Desktop/personal/ecomerce-project/back/src/main/java/com/drtx/ecomerce/amazon/core/model/[modulo]/NuevaClase.java

# 2. Compilar para verificar
./gradlew compileJava

# 3. Commit
git add src/main/java/.../NuevaClase.java
git commit -m "feat: Agregar NuevaClase al modelo de dominio"
```

### Crear un nuevo controlador
```bash
# Estructura típica:
# src/main/java/.../adapters/in/rest/[modulo]/
# ├── NuevoController.java
# ├── dtos/
# │   ├── NuevoRequest.java
# │   └── NuevoResponse.java
# └── mappers/
#     └── NuevoRestMapper.java

./gradlew compileJava
./gradlew test
git add .
git commit -m "feat: Agregar nuevo endpoint"
```

### Agregar validación a un DTO
```bash
# 1. Editar el DTO
vim src/main/java/.../dtos/NuevoRequest.java

# 2. Agregar anotaciones @NotNull, @NotBlank, etc
# 3. Compilar
./gradlew compileJava

# 4. Commit
git commit -m "feat: Agregar validaciones a NuevoRequest"
```

### Cambiar estructura de base de datos
```bash
# 1. Crear Entity
vim src/main/java/.../adapters/out/persistence/[modulo]/NuevaEntity.java

# 2. Crear PersistenceMapper
vim src/main/java/.../adapters/out/persistence/[modulo]/NuevaPersistenceMapper.java

# 3. Crear Repository
vim src/main/java/.../adapters/out/persistence/[modulo]/NuevaRepository.java

# 4. Crear RepositoryAdapter
vim src/main/java/.../adapters/out/persistence/[modulo]/NuevaRepositoryAdapter.java

# 5. Compilar y probar
./gradlew build

git commit -m "feat: Agregar persistencia para Nueva entidad"
```

---

## 🐛 DEBUGGING

### Ejecutar con debug
```bash
./gradlew bootRun --debug
```

### Ver logs durante ejecución
```bash
# Agregar a application.yml:
logging:
  level:
    com.drtx.ecomerce.amazon: DEBUG
    org.springframework.web: DEBUG
```

### Rastrear una clase específica
```bash
grep -rn "CartController" src/ --include="*.java"
```

### Ver qué métodos usa una clase
```bash
grep -n "public\|private" src/main/java/.../CartController.java
```

---

## 📊 ANÁLISIS DEL CÓDIGO

### Ver métodos de una clase
```bash
grep -E "^\s*(public|private|protected)" src/main/java/.../CartController.java
```

### Contar clases por tipo
```bash
find src/main/java -name "*Controller.java" | wc -l
find src/main/java -name "*UseCaseImpl.java" | wc -l
find src/main/java -name "*Entity.java" | wc -l
```

### Ver estructura de paquete
```bash
tree -d src/main/java/com/drtx/ecomerce/amazon -L 3
```

---

## 🔧 LIMPIEZA Y MANTENIMIENTO

### Limpiar caché de Gradle
```bash
./gradlew clean
```

### Remover archivos compilados
```bash
rm -rf build/
rm -rf .gradle/
```

### Reiniciar IDE
```bash
# En caso de problemas:
./gradlew clean
./gradlew build -x test
```

---

## 📋 VERIFICACIÓN FINAL

### Checklist de calidad
```bash
#!/bin/bash
set -e

echo "🔍 Verificando proyecto..."

# 1. Compilar
echo "✓ Compilando..."
./gradlew clean compileJava

# 2. Tests
echo "✓ Ejecutando tests..."
./gradlew test

# 3. Build
echo "✓ Construyendo JAR..."
./gradlew build -x test

# 4. Información
echo "✓ Información del proyecto:"
wc -l $(find src -name "*.java") | tail -1

echo "✅ Proyecto verificado correctamente"
```

---

## 📚 RECURSOS ÚTILES

### Documentación de Spring Boot
```
https://spring.io/projects/spring-boot
```

### Documentación de Spring Security
```
https://spring.io/projects/spring-security
```

### Gradle
```
https://gradle.org/
```

### JPA/Hibernate
```
https://hibernate.org/orm/
```

---

## 💡 TIPS PRODUCTIVOS

### Crear alias para comandos frecuentes
```bash
# En ~/.bashrc o ~/.zshrc, agregar:
alias cdb="cd /home/david/Desktop/personal/ecomerce-project/back"
alias cbuild="./gradlew build -x test"
alias ctest="./gradlew test"
alias crun="./gradlew bootRun"
alias cclean="./gradlew clean"

# Luego usar:
cdb
cbuild
crun
```

### Script para limpiar y compilar
```bash
#!/bin/bash
cd /home/david/Desktop/personal/ecomerce-project/back
echo "🧹 Limpiando..."
./gradlew clean
echo "🔨 Compilando..."
./gradlew build -x test
echo "✅ Done!"
```

### Ver cambios recientes
```bash
git log --oneline -10
git diff HEAD~1
```

---

## 🚨 SOLUCIÓN DE PROBLEMAS COMUNES

### Error: "Gradle daemon crashed"
```bash
./gradlew --stop
./gradlew build -x test --no-daemon
```

### Error: "Port 8080 already in use"
```bash
# Encontrar proceso
lsof -i :8080

# O usar puerto diferente
./gradlew bootRun --args='--server.port=8081'
```

### Error: "Out of memory"
```bash
# Aumentar heap
export GRADLE_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
./gradlew build
```

### Tests fallando aleatoriamente
```bash
# Ejecutar con seed fijo
./gradlew test --tests "*ControllerTest" -Dgroovy.randomize=false
```

---

## 📝 COMANDOS GIT ÚTILES

### Ver estado
```bash
git status
```

### Ver cambios no staged
```bash
git diff
```

### Ver cambios staged
```bash
git diff --staged
```

### Ver historial
```bash
git log --oneline -10
```

### Crear branch
```bash
git checkout -b feature/nueva-funcionalidad
```

### Cambiar de branch
```bash
git checkout nombre-branch
```

### Hacer stash de cambios
```bash
git stash
```

### Recuperar stash
```bash
git stash pop
```

---

**Última actualización:** 26 de Diciembre, 2024
**Proyecto:** E-Commerce Hexagonal + DDD

