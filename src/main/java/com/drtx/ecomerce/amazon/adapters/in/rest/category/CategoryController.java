package com.drtx.ecomerce.amazon.adapters.in.rest.category;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers.CategoryRestMapper;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryUseCasePort categoryUseCasePort;
    private final CategoryRestMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryUseCasePort.getAllCategories();
        return ResponseEntity.ok(
                categories.stream().map(categoryMapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody @jakarta.validation.Valid CategoryRequest category) {
        Category newCategory = categoryMapper.toDomain(category);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.createCategory(newCategory)));
    }

    /**
     * Get category by UUID (main endpoint)
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<CategoryResponse> getCategoryByUuid(@PathVariable UUID uuid) {
        return categoryUseCasePort.getCategoryByUuid(uuid)
                .map(categoryMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update category by UUID (main endpoint)
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<CategoryResponse> updateCategoryByUuid(@PathVariable UUID uuid,
            @RequestBody @jakarta.validation.Valid CategoryRequest categoryRequest) {
        Category category = categoryMapper.toDomain(categoryRequest);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.updateCategoryByUuid(uuid, category)));
    }

    /**
     * Delete category by UUID (main endpoint)
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCategoryByUuid(@PathVariable UUID uuid) {
        categoryUseCasePort.deleteCategoryByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    // Legacy endpoints using Long ID (deprecated)

    /**
     * @deprecated Use {@link #getCategoryByUuid(UUID)} instead
     */
    @Deprecated
    @GetMapping("/by-id/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryUseCasePort.getCategoryById(id)
                .map(categoryMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Use {@link #updateCategoryByUuid(UUID, CategoryRequest)} instead
     */
    @Deprecated
    @PutMapping("/by-id/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CategoryRequest categoryRequest) {
        Category category = categoryMapper.toDomain(categoryRequest);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.updateCategory(id, category)));
    }

    /**
     * @deprecated Use {@link #deleteCategoryByUuid(UUID)} instead
     */
    @Deprecated
    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryUseCasePort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
