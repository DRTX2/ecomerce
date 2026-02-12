package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(ProductRepositoryAdapter.class)
@DisplayName("Product Repository Adapter Integration Tests")
class ProductRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private ProductRepositoryAdapter adapter;

    @Autowired
    private ProductPersistenceRepository productRepository;

    @Autowired
    private CategoryPersistenceRepository categoryRepository;

    @MockitoBean
    private ProductPersistenceMapper mapper;

    @MockitoBean
    private ProductMapperHelper mapperHelper;

    @Test
    @DisplayName("Should save a new product")
    void testSave() {
        // Given
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Electronics");
        categoryEntity.setUuid(UUID.randomUUID());
        categoryEntity = categoryRepository.save(categoryEntity);

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));

        ProductEntity entity = new ProductEntity();
        entity.setName("Laptop");
        entity.setPrice(new BigDecimal("999.99"));
        entity.setDescription("Desc");
        entity.setCategory(categoryEntity);
        entity.setUuid(UUID.randomUUID());
        entity.setSku("LAP-123");
        entity.setSlug("laptop");
        entity.setStockQuantity(100);
        entity.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);

        // Mocks
        when(mapper.toEntity(product)).thenReturn(entity);
        when(mapper.toDomain(any(ProductEntity.class))).thenAnswer(inv -> {
            ProductEntity e = inv.getArgument(0);
            Product p = new Product();
            p.setId(e.getId());
            p.setName(e.getName());
            return p;
        });

        // When
        Product savedProduct = adapter.save(product);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Laptop");
        assertThat(productRepository.findById(savedProduct.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindByUuid() {
        // Given
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Books");
        categoryEntity.setUuid(UUID.randomUUID());
        categoryEntity = categoryRepository.save(categoryEntity);

        ProductEntity entity = new ProductEntity();
        entity.setName("Java Programming");
        entity.setCategory(categoryEntity);
        entity.setDescription("Learn Java");
        entity.setSku("JAVA-123");
        entity.setSlug("java-programming");
        entity.setUuid(UUID.randomUUID());
        entity.setStockQuantity(50);
        entity.setPrice(BigDecimal.TEN);
        entity.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        entity = productRepository.save(entity);

        when(mapper.toDomain(any(ProductEntity.class))).thenAnswer(inv -> {
            ProductEntity e = inv.getArgument(0);
            Product p = new Product();
            p.setId(e.getId());
            p.setUuid(e.getUuid());
            p.setName(e.getName());
            return p;
        });

        // When
        Optional<Product> found = adapter.findByUuid(entity.getUuid());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("Should update product")
    void testUpdateByUuid() {
        // Given
        CategoryEntity cat1 = new CategoryEntity();
        cat1.setName("Cat1");
        cat1.setUuid(UUID.randomUUID());
        cat1 = categoryRepository.save(cat1);

        ProductEntity entity = new ProductEntity();
        entity.setName("Old Name");
        entity.setDescription("Desc");
        entity.setPrice(BigDecimal.ONE);
        entity.setCategory(cat1);
        entity.setSku("SKU-UPD");
        entity.setSlug("old-name");
        entity.setUuid(UUID.randomUUID());
        entity.setStockQuantity(50);
        entity.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        entity = productRepository.save(entity);

        Product updateData = new Product();
        updateData.setName("New Name");
        updateData.setDescription("Desc");
        updateData.setPrice(BigDecimal.TEN);
        updateData.setCategory(new Category(cat1.getId(), cat1.getUuid(), "Cat1", null, null));

        when(mapper.toDomain(any(ProductEntity.class))).thenAnswer(inv -> {
            ProductEntity e = inv.getArgument(0);
            Product p = new Product();
            p.setId(e.getId());
            p.setUuid(e.getUuid());
            p.setName(e.getName());
            return p;
        });

        // When
        Product updated = adapter.updateByUuid(entity.getUuid(), updateData);

        // Then
        assertThat(updated.getName()).isEqualTo("New Name");
        ProductEntity saved = productRepository.findById(entity.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should delete product")
    void testDeleteByUuid() {
        // Given
        CategoryEntity cat = new CategoryEntity();
        cat.setName("Cat");
        cat.setUuid(UUID.randomUUID());
        cat = categoryRepository.save(cat);

        ProductEntity entity = new ProductEntity();
        entity.setName("ToDel");
        entity.setDescription("D");
        entity.setPrice(BigDecimal.ONE);
        entity.setCategory(cat);
        entity.setSku("SKU-DEL");
        entity.setSlug("todel");
        entity.setUuid(UUID.randomUUID());
        entity.setStockQuantity(50);
        entity.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        entity = productRepository.save(entity);

        // When
        adapter.deleteByUuid(entity.getUuid());

        // Then
        assertThat(productRepository.findById(entity.getId())).isEmpty();
    }
}
