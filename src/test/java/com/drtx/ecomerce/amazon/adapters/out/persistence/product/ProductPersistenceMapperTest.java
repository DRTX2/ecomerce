package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ProductPersistenceMapperImpl.class})
class ProductPersistenceMapperTest {

    @Autowired
    private ProductPersistenceMapper productPersistenceMapper;

    @Test
    void toEntity_ShouldMapCorrectly() {
        Category domainCategory = new Category(1L, UUID.randomUUID(), "Electronics", "Electronics description", null);
        Product domainProduct = new Product(1L, UUID.randomUUID(), "Laptop", "Laptop description", new BigDecimal("1000.00"), domainCategory, new BigDecimal("4.5"), List.of("image1.jpg", "image2.jpg"), "SKU123", 10, ProductStatus.ACTIVE, "laptop", LocalDateTime.now(), LocalDateTime.now());

        ProductEntity entity = productPersistenceMapper.toEntity(domainProduct);

        assertNotNull(entity);
        assertEquals(domainProduct.getId(), entity.getId());
        assertEquals(domainProduct.getUuid(), entity.getUuid());
        assertEquals(domainProduct.getName(), entity.getName());
        assertEquals(domainProduct.getCategory().getId(), entity.getCategory().getId());
    }

    @Test
    void toDomain_ShouldMapCorrectly() {
        CategoryEntity entityCategory = new CategoryEntity();
        entityCategory.setId(1L);
        entityCategory.setUuid(UUID.randomUUID());
        entityCategory.setName("Electronics");
        entityCategory.setDescription("Electronics description");

        ProductEntity entityProduct = new ProductEntity();
        entityProduct.setId(1L);
        entityProduct.setUuid(UUID.randomUUID());
        entityProduct.setName("Laptop");
        entityProduct.setDescription("Laptop description");
        entityProduct.setPrice(new BigDecimal("1000.00"));
        entityProduct.setCategory(entityCategory);
        entityProduct.setAverageRating(new BigDecimal("4.5"));
        entityProduct.setSku("SKU123");
        entityProduct.setStockQuantity(10);
        entityProduct.setStatus(ProductStatus.ACTIVE);
        entityProduct.setSlug("laptop");
        entityProduct.setCreatedAt(LocalDateTime.now());
        entityProduct.setUpdatedAt(LocalDateTime.now());

        Product domain = productPersistenceMapper.toDomain(entityProduct);

        assertNotNull(domain);
        assertEquals(entityProduct.getId(), domain.getId());
        assertEquals(entityProduct.getUuid(), domain.getUuid());
        assertEquals(entityProduct.getName(), domain.getName());
        assertEquals(entityProduct.getCategory().getUuid(), domain.getCategory().getUuid());
    }
}
