package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    Category updateById(Long id, Category category) throws EntityNotFoundException;
    void delete(Long id);
}
