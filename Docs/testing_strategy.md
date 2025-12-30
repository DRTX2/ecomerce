# 🧪 Testing Strategy & Quality Assurance

Quality is not an afterthought; it is built into the development lifecycle. We employ a comprehensive testing pyramid to ensure robustness.

## 📐 The Testing Pyramid

### 1. Unit Tests (Foundation)
- **Scope**: Individual classes (Use Cases, Domain Models).
- **Tools**: `JUnit 5`, `Mockito`, `AssertJ`.
- **Logic**: We mock all external dependencies (`Ports`) to test business logic in isolation.
- **Example**: `ProductUseCaseImplTest.java` verifies that business rules (like zero stock handling) work without touching the database.

```java
// Logic verification without DB context
@Test
void shouldThrowExceptionWhenStockIsZero() {
    when(repo.findById(1L)).thenReturn(productWithZeroStock);
    assertThrows(OutofStockException.class, () -> useCase.addToCart(1L));
}
```

### 2. Integration Tests (Adapters)
- **Scope**: Interaction between Adapters and External Systems (DB, API).
- **Tools**: `Spring Boot Test`, `H2 (In-memory DB)`, `TestContainers` (optional).
- **Goal**: Verify that our SQL queries are correct and Mappers work as expected.
- **Example**: `ProductRepositoryAdapterTest.java` saves a real entity to H2 and retrieves it to ensure mapping fidelity.

### 3. Controller Tests (Slices)
- **Scope**: HTTP Layer.
- **Tools**: `MockMvc`.
- **Goal**: Ensure REST endpoints accept correct JSON, validate input (`@Valid`), and return correct HTTP status codes.

## ✅ Naming Conventions
We follow a strict naming pattern for clarity:
`should[ExpectedBehavior]When[State]`

- `shouldReturnProduct_WhenIdExists`
- `shouldThrowNotFound_WhenIdDoesNotExist`

## 🛡️ Security Testing
We test our security layer (`SecurityConfig`) to ensure:
- Public endpoints (`/auth/login`) are accessible.
- Protected endpoints (`/orders`) reject unauthenticated requests (`401 Unauthorized`).
