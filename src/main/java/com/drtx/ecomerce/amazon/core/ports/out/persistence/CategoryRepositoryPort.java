package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);

    Optional<Category> findById(Long id);
    Optional<Category> findByUuid(UUID uuid);

    List<Category> findAll();

    Category updateById(Long id, Category category) throws EntityNotFoundException;
    Category updateByUuid(UUID uuid, Category category) throws EntityNotFoundException;

    void delete(Long id);
    void deleteByUuid(UUID uuid);
}
