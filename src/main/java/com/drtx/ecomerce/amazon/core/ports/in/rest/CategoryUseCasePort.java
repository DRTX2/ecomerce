package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.product.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryUseCasePort {
    Category createCategory(Category category);

    Optional<Category> getCategoryById(Long id);
    Optional<Category> getCategoryByUuid(UUID uuid);

    List<Category> getAllCategories();

    Category updateCategory(Long id, Category category);
    Category updateCategoryByUuid(UUID uuid, Category category);

    void deleteCategory(Long id);
    void deleteCategoryByUuid(UUID uuid);
}
