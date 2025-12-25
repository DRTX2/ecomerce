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

@RestController
@RequestMapping("/categories/")
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

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryUseCasePort.getCategoryById(id).map(categoryMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CategoryRequest categoryRequest) {
        Category category = categoryMapper.toDomain(categoryRequest);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.updateCategory(id, category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryUseCasePort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
