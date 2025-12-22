package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import com.drtx.ecomerce.amazon.core.model.Category;
import jakarta.persistence.EntityNotFoundException;
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
        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Devices");

        CategoryEntity entity = new CategoryEntity();
        entity.setName("Electronics");
        entity.setDescription("Devices");
        // ID null for insert

        when(mapper.toEntity(category)).thenReturn(entity);
        when(mapper.toDomain(any(CategoryEntity.class))).thenAnswer(inv -> {
            CategoryEntity e = inv.getArgument(0);
            Category c = new Category();
            c.setId(e.getId());
            c.setName(e.getName());
            return c;
        });

        // When
        Category savedCategory = adapter.save(category);

        // Then
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Electronics");
        assertThat(repository.findById(savedCategory.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find category by ID")
    void testFindById() {
        // Given
        CategoryEntity entity = new CategoryEntity(null, "Books", "Reading", null);
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
        repository.save(new CategoryEntity(null, "C1", "D1", null));
        repository.save(new CategoryEntity(null, "C2", "D2", null));

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
        CategoryEntity entity = repository.save(new CategoryEntity(null, "Old", "Old Desc", null));

        Category updateData = new Category();
        updateData.setName("New");
        updateData.setDescription("New Desc");

        Category updatedDomain = new Category();
        updatedDomain.setName("New");
        updatedDomain.setId(entity.getId());

        when(mapper.toDomain(any(CategoryEntity.class))).thenReturn(updatedDomain);

        // When
        Category updated = adapter.updateById(entity.getId(), updateData);

        // Then
        assertThat(updated.getName()).isEqualTo("New");

        CategoryEntity fromDb = repository.findById(entity.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("New");
        assertThat(fromDb.getDescription()).isEqualTo("New Desc");
    }

    @Test
    @DisplayName("Should delete category")
    void testDelete() {
        // Given
        CategoryEntity entity = repository.save(new CategoryEntity(null, "ToDel", "D", null));

        // When
        adapter.delete(entity.getId());

        // Then
        assertThat(repository.existsById(entity.getId())).isFalse();
    }
}
