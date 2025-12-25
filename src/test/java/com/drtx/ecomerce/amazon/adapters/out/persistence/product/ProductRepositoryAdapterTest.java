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
        categoryEntity = categoryRepository.save(categoryEntity);

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));

        ProductEntity entity = new ProductEntity();
        entity.setName("Laptop");
        entity.setPrice(new BigDecimal("999.99"));
        entity.setDescription("Desc");
        entity.setCategory(categoryEntity);

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
    void testFindById() {
        // Given
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Books");
        categoryEntity = categoryRepository.save(categoryEntity);

        ProductEntity entity = new ProductEntity();
        entity.setName("Clean Code");
        entity.setDescription("Book");
        entity.setPrice(BigDecimal.TEN);
        entity.setCategory(categoryEntity);
        entity = productRepository.save(entity);

        Product domainProduct = new Product();
        domainProduct.setId(entity.getId());
        domainProduct.setName("Clean Code");

        when(mapper.toDomain(any(ProductEntity.class))).thenReturn(domainProduct);

        // When
        Optional<Product> found = adapter.findById(entity.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should update product")
    void testUpdateById() {
        // Given
        CategoryEntity cat1 = categoryRepository.save(new CategoryEntity(null, "Cat1", null, null));
        CategoryEntity cat2 = categoryRepository.save(new CategoryEntity(null, "Cat2", null, null));

        ProductEntity entity = new ProductEntity(null, "Old Name", "Desc", BigDecimal.ONE, cat1,
                BigDecimal.valueOf(5), null);
        entity = productRepository.save(entity);

        Product updateData = new Product();
        updateData.setName("New Name");
        updateData.setDescription("New Desc");
        updateData.setPrice(BigDecimal.TEN);
        updateData.setAverageRating(BigDecimal.valueOf(4.5));
        updateData.setImages(Collections.emptyList());

        Category domainCategory = new Category();
        domainCategory.setId(cat2.getId()); // Switching to Cat2
        updateData.setCategory(domainCategory);

        when(mapperHelper.mapToEntities(anyList())).thenReturn(Collections.emptyList());
        when(mapper.toDomain(any(ProductEntity.class))).thenAnswer(inv -> {
            ProductEntity e = inv.getArgument(0);
            Product p = new Product();
            p.setName(e.getName());
            p.setId(e.getId());
            return p;
        });

        // When
        Product updated = adapter.updateById(entity.getId(), updateData);

        // Then
        assertThat(updated.getName()).isEqualTo("New Name");

        ProductEntity fromDb = productRepository.findById(entity.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("New Name");
        assertThat(fromDb.getCategory().getId()).isEqualTo(cat2.getId()); // Verify category change
    }

    @Test
    @DisplayName("Should delete product")
    void testDelete() {
        // Given
        CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "Cat", null, null));
        ProductEntity entity = productRepository
                .save(new ProductEntity(null, "ToDel", "D", BigDecimal.ONE, cat, BigDecimal.ONE, null));

        // When
        adapter.delete(entity.getId());

        // Then
        assertThat(productRepository.existsById(entity.getId())).isFalse();
    }
}
