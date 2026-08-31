# Esquema Actual de Persistencia

Fuente de verdad: migraciones Flyway `V1` a `V6` en `src/main/resources/db/migration/`. Este diagrama describe tablas existentes, no garantiza que todos los modulos tengan un flujo de aplicacion terminado.

```mermaid
erDiagram
    USERS ||--o{ CARTS : owns
    USERS ||--o{ ORDERS : places
    USERS ||--o{ REVIEWS : writes
    USERS ||--o{ FAVORITE_PUBLICATIONS : saves
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ REPORTS : creates
    USERS ||--o{ INCIDENCES : moderates
    USERS ||--o{ APPEALS : seller_or_moderator
    USERS ||--o{ REFRESH_TOKENS : owns

    CATEGORIES ||--o{ PRODUCTS : classifies
    PRODUCTS ||--o{ PRODUCT_IMAGES : has
    PRODUCTS ||--o{ CART_ITEMS : appears_in
    PRODUCTS ||--o{ ORDER_ITEMS : purchased_as
    PRODUCTS ||--o{ REVIEWS : receives
    PRODUCTS ||--o{ INVENTORIES : stocked_as
    PRODUCTS ||--o{ FAVORITE_PUBLICATIONS : favorited
    PRODUCTS ||--o{ INCIDENCES : reported_about
    PRODUCTS }o--o{ DISCOUNTS : discount_products
    PRODUCTS }o--o{ SUPPLIERS : supplier_products

    CARTS ||--o{ CART_ITEMS : contains
    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDERS ||--o{ PAYMENTS : has
    ORDERS ||--o{ SHIPPINGS : has
    ORDERS ||--o{ ORDER_RETURNS : has
    ORDERS }o--o{ DISCOUNTS : order_discounts
    INCIDENCES ||--o{ REPORTS : contains
    INCIDENCES ||--o{ APPEALS : receives

    USERS {
        bigint id PK
        varchar email
        varchar password_hash
        varchar role
    }
    PRODUCTS {
        bigint id PK
        bigint category_id FK
        varchar sku UK
        varchar slug UK
        int stock_quantity
        varchar status
        decimal price
    }
    ORDERS {
        bigint id PK
        bigint user_id FK
        varchar order_state
        decimal total
        timestamp created_at
    }
    INCIDENCES {
        bigint id PK
        uuid public_ui UK
        bigint product_id FK
        bigint moderator_id FK
        varchar status
    }
    APPEALS {
        bigint id PK
        bigint incidence_id FK
        bigint seller_id FK
        bigint new_moderator_id FK
        varchar status
    }
    REFRESH_TOKENS {
        bigint id PK
        bigint user_id FK
        varchar token
        timestamp expires_at
    }
```

## Notas de evolucion

- `payments` existe por scaffolding, pero no forma parte del alcance actual.
- `products.stock_quantity` es la unica fuente de verdad para stock vendible y reservas. `inventories.available_quantity` queda fuera del checkout hasta que se modele como asignacion por bodega derivada.
- Las tablas `orders`, `payments` y `shippings` no imponen cardinalidad uno a uno por constraint; el modelo de aplicacion debe definirla o una migracion futura debe reforzarla.
- `outbox_events` y `audit_logs` se crean en `V7`. La confirmacion de pedido escribe ambos registros en la misma transaccion que el pedido y la reserva de stock.
