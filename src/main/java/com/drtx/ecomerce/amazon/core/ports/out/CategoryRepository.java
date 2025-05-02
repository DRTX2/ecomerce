package com.drtx.ecomerce.amazon.core.ports.out;

import com.drtx.ecomerce.amazon.core.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    Category updateById(Long id, Category category);
    void delete(Long id);
}
