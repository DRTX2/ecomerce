# 🔌 API & GraphQL Reference

This API serves as the backbone of the E-commerce platform, providing both RESTful endpoints for standard operations and GraphQL for optimization-heavy queries.

## 🌐 REST API

**Base URL**: `http://localhost:8080/api/v1`
**Content-Type**: `application/json`

### Authentication
Secure access using JWT (Json Web Tokens).

#### Login
`POST /auth/login`
```json
// Request
{
  "email": "user@example.com",
  "password": "securePassword123"
}

// Response
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresAt": "2024-12-31T23:59:59"
}
```

### Product Management

#### Create Product (Admin)
`POST /products`
```json
{
  "name": "Gaming Laptop",
  "description": "High performance...",
  "price": 1299.99,
  "stock": 50,
  "categoryId": 1
}
```

#### Get All Products
`GET /products`
- **Response**: Array of product objects.

### Errors
The API uses standard HTTP codes.
- `200 OK`: Success.
- `201 Created`: Resource created.
- `400 Bad Request`: Validation failure.
- `401 Unauthorized`: Missing or invalid JWT.
- `404 Not Found`: Resource does not exist.
- `500 Internal Error`: Server issue.

```json
// Error Example
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 123 not found",
  "path": "/api/v1/products/123"
}
```

---

## ⚛️ GraphQL API

Designed for the **Customer Support** module to prevent over-fetching data.

**Endpoint**: `/graphql`
**Tooling**: Use GraphiQL at `/graphiql` (dev mode only)

### Schema Example

#### Query: Get Incidence Details
Fetch only what you need.
```graphql
query {
  getIncidence(id: "101") {
    id
    subject
    status
    createdAt
    messages {
      content
      sender
    }
  }
}
```

#### Mutation: Create Appeal
```graphql
mutation {
  createAppeal(input: {
    incidenceId: "101",
    reason: "I disagree with the refund decision"
  }) {
    id
    status
  }
}
```
