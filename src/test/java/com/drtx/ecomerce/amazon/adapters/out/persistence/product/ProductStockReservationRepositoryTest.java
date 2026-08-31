package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductStockReservationRepositoryTest {
    @Autowired
    private ProductPersistenceRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void reserveStockNeverDecrementsBelowZero() {
        ProductEntity product = new ProductEntity(null, "Keyboard", "Mechanical keyboard", BigDecimal.TEN,
                null, null, null, "KEYBOARD-001", 2, ProductStatus.ACTIVE, "keyboard-001", null, null);
        product = repository.saveAndFlush(product);

        assertThat(repository.reserveStock(product.getId(), 2)).isEqualTo(1);
        assertThat(repository.reserveStock(product.getId(), 1)).isZero();

        entityManager.clear();
        assertThat(repository.findById(product.getId()).orElseThrow().getStockQuantity()).isZero();
    }
}
