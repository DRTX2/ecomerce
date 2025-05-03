package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryServicePort {
    Category createCategory(Category category);
    Optional<Category> getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);

}
