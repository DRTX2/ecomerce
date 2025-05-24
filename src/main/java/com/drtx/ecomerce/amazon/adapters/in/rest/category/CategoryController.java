package com.drtx.ecomerce.amazon.adapters.in.rest.category;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers.CategoryRestMapper;
import com.drtx.ecomerce.amazon.core.model.Category;
import com.drtx.ecomerce.amazon.core.ports.in.CategoryServicePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories/")
@AllArgsConstructor
public class CategoryController {
    private final CategoryServicePort categoryServicePort;
    private final CategoryRestMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryServicePort.getAllCategories();
        return ResponseEntity.ok(
                categories.
                        stream().
                        map(categoryMapper::toResponse).
                        toList()
        );
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest category) {
        Category newCategory = categoryMapper.toDomain(category);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryServicePort.createCategory(newCategory))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryServicePort.
                getCategoryById(id).
                map(categoryMapper::toResponse).
                map(ResponseEntity::ok).
                orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest){
        Category category=categoryMapper.toDomain(categoryRequest);
        return ResponseEntity.ok(categoryMapper.toResponse(
                        categoryServicePort.updateCategory(id,category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable  Long id){
        categoryServicePort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
