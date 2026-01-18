# Product Search & Filtering - Implementation Summary

## 📋 Overview
Implementación completa de búsqueda de productos con filtros avanzados, paginación, productos populares y ofertas, siguiendo arquitectura hexagonal.

## 🎯 Características Implementadas

### 1. **Búsqueda con Filtros** (`/products/search`)
Permite buscar productos con múltiples criterios:

#### Filtros Disponibles:
- **Búsqueda de texto**: `searchTerm` (busca en nombre, descripción y SKU)
- **Categoría**: `categoryUuid`
- **Estado**: `status` (ACTIVE, DRAFT, ARCHIVED)
- **Rango de precio**: `minPrice`, `maxPrice`
- **Rango de rating**: `minRating`, `maxRating`
- **Productos en oferta**: `onSale` (con descuento > 0%)
- **En stock**: `inStock` (stock > 0)
- **Destacados**: `featuredOnly`

#### Paginación y Ordenamiento:
- **page**: Número de página (default: 0)
- **size**: Tamaño de página (default: 20)
- **sortBy**: Campo para ordenar (default: "createdAt")
- **sortDirection**: ASC o DESC (default: DESC)

#### Ejemplo de Uso:
```http
GET /api/v1/products/search?searchTerm=laptop&minPrice=500&maxPrice=2000&inStock=true&page=0&size=20&sortBy=price&sortDirection=ASC
```

### 2. **Productos Populares** (`/products/popular`)
Retorna productos más populares basados en:
- Número de favoritos (más favoritos = más popular)
- Rating promedio (mayor rating = mejor posicionamiento)
- Fecha de creación (productos nuevos de desempate)

#### Ejemplo de Uso:
```http
GET /api/v1/products/popular?limit=12
```

### 3. **Productos en Oferta** (`/products/deals`)
Retorna productos con descuentos activos ordenados por:
- Porcentaje de descuento (mayor descuento primero)
- Fecha de creación

#### Ejemplo de Uso:
```http
GET /api/v1/products/deals?limit=10
```

## 🏗️ Arquitectura Implementada

### Domain Layer (Core)
```
core/
├── model/
│   ├── product/
│   │   ├── Product.java (añadidos: discountPercentage, featured)
│   │   └── ProductSearchCriteria.java (nuevo)
│   └── pagination/
│       ├── PageRequest.java (existente)
│       ├── PageResponse.java (nuevo)
│       └── SortDirection.java (existente)
└── ports/
    ├── in/
    │   └── rest/
    │       └── ProductUseCasePort.java (actualizado)
    └── out/
        └── persistence/
            └── ProductRepositoryPort.java (actualizado)
```

### Application Layer
```
application/usecases/product/
└── ProductUseCaseImpl.java
    ├── searchProducts() → búsqueda paginada
    ├── getPopularProducts() → productos populares
    └── getDealsProducts() → productos en oferta
```

### Infrastructure Layer (Adapters)

#### Persistence (OUT)
```
adapters/out/persistence/product/
├── ProductEntity.java (añadidos: discountPercentage, featured, favorites)
├── ProductPersistenceRepository.java (+ JpaSpecificationExecutor)
├── ProductSpecifications.java (nuevo - JPA Criteria API)
└── ProductRepositoryAdapter.java (implementa búsqueda)
```

#### REST API (IN)
```
adapters/in/rest/product/
├── ProductController.java
│   ├── GET /search → búsqueda con filtros
│   ├── GET /popular → productos populares
│   └── GET /deals → productos en oferta
├── dto/
│   └── ProductSearchRequest.java (nuevo)
└── mappers/
    └── ProductRestMapper.java (añadido toCriteria())
```

## 🗄️ Database Changes

### Migración V8 - Nuevos Campos
```sql
ALTER TABLE products
ADD COLUMN discount_percentage DECIMAL(5,2) DEFAULT 0.00,
ADD COLUMN featured BOOLEAN DEFAULT FALSE;

-- Índices para optimizar búsquedas
CREATE INDEX idx_products_discount ON products(discount_percentage) WHERE discount_percentage > 0;
CREATE INDEX idx_products_featured ON products(featured) WHERE featured = TRUE;
CREATE INDEX idx_products_status_stock ON products(status, stock_quantity);
```

## 🔍 JPA Specifications - Dynamic Queries

La clase `ProductSpecifications` utiliza **Criteria API** para construir queries dinámicas:

### Ventajas:
✅ **Type-safe**: Compilación verifica tipos  
✅ **Dinámico**: Solo aplica filtros presentes  
✅ **Performante**: Genera SQL óptimo  
✅ **Mantenible**: Lógica de query centralizada

### Ejemplo de Query Generada:
```sql
SELECT p FROM products p
LEFT JOIN p.category c
LEFT JOIN p.favorites f
WHERE 
    LOWER(p.name) LIKE '%laptop%'
    AND p.price BETWEEN 500 AND 2000
    AND p.stock_quantity > 0
    AND p.status = 'ACTIVE'
GROUP BY p.id
ORDER BY p.price ASC
LIMIT 20 OFFSET 0
```

## 📊 Performance Considerations

### 1. **Índices de Base de Datos**
- `idx_products_discount`: Para filtrar productos en oferta
- `idx_products_featured`: Para filtrar productos destacados
- `idx_products_status_stock`: Para filtrar por estado y stock

### 2. **Queries Optimizadas**
- **Popular Products**: Usa LEFT JOIN con agregación (COUNT de favoritos)
- **Deals Products**: Filtro directo con índice en `discount_percentage`
- **Search**: Specifications generan queries con solo los filtros necesarios

### 3. **Paginación**
- Siempre retorna resultados paginados
- Límite máximo de 100 items por request (previene abuse)
- Default: 20 items por página

## 🔒 Security & Validation

### Input Validation:
- **Límites**: Popular y Deals limitan entre 1-100
- **Paginación**: Valores válidos (page >= 0, size > 0)
- **UUID**: Validación automática de Spring

### Business Rules:
- Solo productos `ACTIVE` aparecen en búsquedas públicas
- Popular products requieren `stock > 0`
- Deals products requieren `discount > 0` y `stock > 0`

## 📝 Response Examples

### Search Response
```json
{
  "content": [
    {
      "id": 1,
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Laptop Gaming",
      "price": 1299.99,
      "discountPercentage": 15.0,
      "featured": true,
      "stockQuantity": 50,
      "status": "ACTIVE"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

### Popular/Deals Response
```json
[
  {
    "id": 1,
    "name": "Laptop Gaming",
    "price": 1299.99,
    ...
  }
]
```

## 🚀 Usage Tips

### Frontend Integration

#### React/Next.js Example:
```typescript
// Search products
const searchProducts = async (filters: ProductFilters) => {
  const params = new URLSearchParams({
    searchTerm: filters.search,
    minPrice: filters.minPrice?.toString(),
    maxPrice: filters.maxPrice?.toString(),
    page: filters.page.toString(),
    size: '20',
    sortBy: filters.sortBy || 'createdAt',
    sortDirection: filters.sortDir || 'DESC'
  });
  
  const response = await fetch(`/api/v1/products/search?${params}`);
  return response.json();
};

// Get popular products for homepage
const getPopularProducts = async () => {
  const response = await fetch('/api/v1/products/popular?limit=12');
  return response.json();
};

// Get deals for promotions section
const getDeals = async () => {
  const response = await fetch('/api/v1/products/deals?limit=10');
  return response.json();
};
```

## ✅ Benefits of This Implementation

1. **Flexible**: Todos los filtros son opcionales
2. **Performant**: Índices y queries optimizadas
3. **Scalable**: Paginación previene overload
4. **Clean**: Arquitectura hexagonal bien aplicada
5. **Type-safe**: Compilación verifica todo
6. **Maintainable**: Código organizado y documentado

## 🔄 Future Enhancements

Posibles mejoras futuras:
- [ ] Caché de productos populares (Redis)
- [ ] Full-text search con Elasticsearch
- [ ] Filtros por múltiples categorías
- [ ] Ordenamiento por relevancia
- [ ] Filtro por vendedor/marca
- [ ] Búsqueda por características/especificaciones

