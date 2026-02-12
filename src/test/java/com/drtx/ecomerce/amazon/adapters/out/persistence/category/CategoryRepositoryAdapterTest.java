package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import com.drtx.ecomerce.amazon.core.model.product.Category;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(CategoryRepositoryAdapter.class)
@DisplayName("Category Repository Adapter Integration Tests")
class CategoryRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private CategoryRepositoryAdapter adapter;

    @Autowired
    private CategoryPersistenceRepository repository;

    @MockitoBean
    private CategoryPersistenceMapper mapper;

    @Test
    @DisplayName("Should save a new category")
    void testSave() {
        // Given
        Category domain = new Category();
        domain.setName("Books");
        domain.setDescription("Reading material");

        CategoryEntity entity = new CategoryEntity();
        entity.setName("Books");
        entity.setDescription("Reading material");
        entity.setUuid(UUID.randomUUID());

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(mapper.toDomain(any(CategoryEntity.class))).thenAnswer(inv -> {
            CategoryEntity e = inv.getArgument(0);
            return new Category(e.getId(), e.getUuid(), e.getName(), e.getDescription(), Collections.emptyList());
        });

        // When
        Category saved = adapter.save(domain);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Books");
        assertThat(repository.findByName("Books")).isPresent();
    }

    @Test
    @DisplayName("Should find category by ID")
    void testFindById() {
        // Given
        CategoryEntity entity = new CategoryEntity();
        entity.setName("Books");
        entity.setDescription("Reading");
        entity.setUuid(UUID.randomUUID());
        entity = repository.save(entity);

        Category domainCategory = new Category();
        domainCategory.setId(entity.getId());
        domainCategory.setName("Books");

        when(mapper.toDomain(any(CategoryEntity.class))).thenReturn(domainCategory);

        // When
        Optional<Category> found = adapter.findById(entity.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Books");
    }

    @Test
    @DisplayName("Should find all categories")
    void testFindAll() {
        // Given
        CategoryEntity c1 = new CategoryEntity();
        c1.setName("C1");
        c1.setUuid(UUID.randomUUID());
        repository.save(c1);

        CategoryEntity c2 = new CategoryEntity();
        c2.setName("C2");
        c2.setUuid(UUID.randomUUID());
        repository.save(c2);

        when(mapper.toDomain(any(CategoryEntity.class))).thenReturn(new Category());

        // When
        List<Category> all = adapter.findAll();

        // Then
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("Should update existing category")
    void testUpdateById() {
        // Given
        CategoryEntity entity = new CategoryEntity();
        entity.setName("Old");
        entity.setDescription("Old Desc");
        entity.setUuid(UUID.randomUUID());
        entity = repository.save(entity);

        Category updateData = new Category();
        updateData.setName("New");
        updateData.setDescription("New Desc");

        when(mapper.toDomain(any(CategoryEntity.class))).thenAnswer(inv -> {
            CategoryEntity e = inv.getArgument(0);
            return new Category(e.getId(), e.getUuid(), e.getName(), e.getDescription(), Collections.emptyList());
        });

        // When
        Category updated = adapter.updateById(entity.getId(), updateData);

        // Then
        assertThat(updated.getName()).isEqualTo("New");
        CategoryEntity fromDb = repository.findById(entity.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("New");
    }

    @Test
    @DisplayName("Should delete category")
    void testDelete() {
        // Given
        CategoryEntity entity = new CategoryEntity();
        entity.setName("ToDel");
        entity.setUuid(UUID.randomUUID());
        entity = repository.save(entity);

        // When
        adapter.delete(entity.getId());

        // Then
        assertThat(repository.existsById(entity.getId())).isFalse();
    }
}
