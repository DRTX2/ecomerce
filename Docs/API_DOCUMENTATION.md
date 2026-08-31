# 📖 API Documentation with Scalar & OpenAPI

This project uses **Springdoc OpenAPI 3** to generate OpenAPI specifications and **Scalar** as the modern API reference UI.

## 🚀 Quick Access URLs

| Interface | URL | Description |
|-----------|-----|-------------|
| **Scalar UI** | `http://localhost:8080/api/v1/scalar` | **Recommended** - Modern, fast, beautiful API reference |
| **Swagger UI** | `http://localhost:8080/api/v1/swagger-ui.html` | Classic Swagger interface |
| **OpenAPI JSON** | `http://localhost:8080/api/v1/v3/api-docs` | Raw OpenAPI 3.1 specification |
| **OpenAPI YAML** | `http://localhost:8080/api/v1/v3/api-docs.yaml` | OpenAPI spec in YAML format |

> **Note**: All URLs include the context path `/api/v1` configured in `server.servlet.context-path`

## 🎨 Scalar Features

Scalar provides a superior API documentation experience:

- **Modern UI**: Clean, responsive design with dark/light mode
- **Interactive Testing**: Built-in API client to test endpoints directly
- **Code Samples**: Auto-generated snippets in cURL, Python, JavaScript, Go, etc.
- **Authentication**: Built-in JWT bearer token support
- **Search**: Instant fuzzy search across all endpoints
- **Offline Support**: Works as PWA (Progressive Web App)

## 🔐 Authentication in Scalar

1. Open Scalar at `/scalar`
2. Click the **🔒 Authorize** button (top right)
3. Enter your JWT token (without "Bearer " prefix)
4. Click **Authorize** - all subsequent requests will include the token

## 📋 API Structure

### Base Path
All REST endpoints are under: `/api/v1`

### Module Endpoints

| Module | Path | Description |
|--------|------|-------------|
| **Auth** | `/api/v1/auth` | Login, registro, refresh tokens |
| **Productos** | `/api/v1/products` | CRUD catálogo, imágenes |
| **Categorías** | `/api/v1/categories` | Gestión de categorías |
| **Carrito** | `/api/v1/cart` | Carrito persistente |
| **Favoritos** | `/api/v1/favorites` | Lista de deseos |
| **Órdenes** | `/api/v1/orders` | Procesamiento de órdenes |
| **Incidencias** | `/api/v1/incidences` | Reportes y gestión |
| **Apelaciones** | `/api/v1/appeals` | Sistema de apelaciones |
| **Usuarios** | `/api/v1/users` | Perfil y gestión de usuarios |

### GraphQL Endpoint
- **GraphQL**: `/api/v1/graphql` (para Incidencias y Apelaciones optimizadas)
- **GraphiQL**: `/api/v1/graphiql` (interfaz de prueba GraphQL)

## 🏷️ OpenAPI Tags

Endpoints are organized by tags matching the module structure:
- `Authentication` - Auth endpoints
- `Productos` - Product catalog
- `Categorías` - Categories
- `Carrito` - Shopping cart
- `Favoritos` - Wishlist
- `Órdenes` - Order processing
- `Incidencias` - Incidence reports
- `Apelaciones` - Appeals
- `Usuarios` - User management

## ⚙️ Configuration

### Springdoc Properties (`application.yml`)

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
  show-actuator: false
  packages-to-scan: com.drtx.ecomerce.amazon.adapters.in.rest
  paths-to-match: /api/v1/**
  version: v1
  cache:
    disabled: true
```

### Security Scheme

JWT Bearer authentication is configured globally:

```java
@SecurityRequirement(name = "bearerAuth")
```

And defined in `OpenApiConfig`:

```java
.addSecuritySchemes("bearerAuth", new SecurityScheme()
    .type(SecurityScheme.Type.HTTP)
    .scheme("bearer")
    .bearerFormat("JWT"))
```

## 🛠️ Adding Documentation to Controllers

Use Springdoc/OpenAPI annotations:

```java
@Tag(name = "Módulo", description = "Descripción del módulo")
@RestController
@RequestMapping("/endpoint")
@SecurityRequirement(name = "bearerAuth")
public class MiController {

    @Operation(summary = "Título corto", description = "Descripción detallada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = MiResponse.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MiResponse> getById(
            @Parameter(description = "ID del recurso", example = "1")
            @PathVariable Long id) {
        // ...
    }
}
```

## 📦 Dependencies

```gradle
// OpenAPI / Scalar API Documentation
implementation "org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVersion}"
implementation "org.springdoc:springdoc-openapi-starter-webmvc-api:${springdocVersion}"

// Scalar API Reference UI
implementation "org.webjars:scalar-api-reference:2024.12.23"
```

## 🔧 Development Tips

### Regenerate Spec on Changes
The OpenAPI spec is generated at runtime. Just restart the application after controller changes.

### Customize Scalar Theme
Create a custom Scalar HTML page in `src/main/resources/static/scalar.html` if needed.

### Export for External Tools
Download the OpenAPI JSON from `/v3/api-docs` and import into:
- Postman
- Insomnia
- Stoplight
- Redocly
- Any OpenAPI 3 compatible tool

### Validate Spec
```bash
# Using swagger-codegen
swagger-codegen validate -i http://localhost:8080/api/v1/v3/api-docs
```

## 📚 Additional Resources

- [Springdoc OpenAPI Reference](https://springdoc.org/)
- [Scalar Documentation](https://scalar.com/)
- [OpenAPI 3.1 Specification](https://spec.openapis.org/oas/v3.1.0)
- [Springdoc Migration Guide](https://springdoc.org/migrating-from-springfox.html)
