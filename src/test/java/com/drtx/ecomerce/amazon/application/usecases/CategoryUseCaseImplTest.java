package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CategoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryUseCaseImpl Unit Tests")
class CategoryUseCaseImplTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private CategoryUseCaseImpl categoryUseCase;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category(
                1L,
                "Electronics",
                "Electronic devices and accessories",
                List.of()
        );
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Given
        Category newCategory = new Category(
                null,
                "Books",
                "Books and literature",
                List.of()
        );

        Category savedCategory = new Category(
                2L,
                "Books",
                "Books and literature",
                List.of()
        );

        when(categoryRepositoryPort.save(any(Category.class))).thenReturn(savedCategory);

        // When
        Category result = categoryUseCase.createCategory(newCategory);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Books");
        assertThat(result.getDescription()).isEqualTo("Books and literature");
        verify(categoryRepositoryPort, times(1)).save(newCategory);
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void shouldGetCategoryByIdSuccessfully() {
        // Given
        Long categoryId = 1L;
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When
        Optional<Category> result = categoryUseCase.getCategoryById(categoryId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(categoryId);
        assertThat(result.get().getName()).isEqualTo("Electronics");
        assertThat(result.get().getDescription()).isEqualTo("Electronic devices and accessories");
        verify(categoryRepositoryPort, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Should return empty when category not found by ID")
    void shouldReturnEmptyWhenCategoryNotFoundById() {
        // Given
        Long categoryId = 999L;
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<Category> result = categoryUseCase.getCategoryById(categoryId);

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepositoryPort, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() {
        // Given
        Category category2 = new Category(
                2L,
                "Clothing",
                "Apparel and fashion",
                List.of()
        );

        Category category3 = new Category(
                3L,
                "Home & Garden",
                "Home improvement and gardening",
                List.of()
        );

        List<Category> categories = Arrays.asList(testCategory, category2, category3);
        when(categoryRepositoryPort.findAll()).thenReturn(categories);

        // When
        List<Category> result = categoryUseCase.getAllCategories();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(testCategory, category2, category3);
        verify(categoryRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        // Given
        when(categoryRepositoryPort.findAll()).thenReturn(List.of());

        // When
        List<Category> result = categoryUseCase.getAllCategories();

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        // Given
        Long categoryId = 1L;
        Category updatedCategory = new Category(
                categoryId,
                "Electronics & Gadgets",
                "Updated description for electronics",
                List.of()
        );

        when(categoryRepositoryPort.updateById(eq(categoryId), any(Category.class)))
                .thenReturn(updatedCategory);

        // When
        Category result = categoryUseCase.updateCategory(categoryId, updatedCategory);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("Electronics & Gadgets");
        assertThat(result.getDescription()).isEqualTo("Updated description for electronics");
        verify(categoryRepositoryPort, times(1)).updateById(categoryId, updatedCategory);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        // Given
        Long categoryId = 1L;
        doNothing().when(categoryRepositoryPort).delete(categoryId);

        // When
        categoryUseCase.deleteCategory(categoryId);

        // Then
        verify(categoryRepositoryPort, times(1)).delete(categoryId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent category")
    void shouldHandleDeleteForNonExistentCategory() {
        // Given
        Long categoryId = 999L;
        doNothing().when(categoryRepositoryPort).delete(categoryId);

        // When
        categoryUseCase.deleteCategory(categoryId);

        // Then
        verify(categoryRepositoryPort, times(1)).delete(categoryId);
    }
}
