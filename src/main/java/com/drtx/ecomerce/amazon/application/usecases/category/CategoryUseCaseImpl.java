package com.drtx.ecomerce.amazon.application.usecases.category;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CategoryRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryUseCaseImpl implements CategoryUseCasePort {
    private final CategoryRepositoryPort repository;

    @Override
    public Category createCategory(Category category) {
        return repository.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Category> getCategoryByUuid(UUID uuid) {
        return repository.findByUuid(uuid);
    }

    @Override
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return repository.updateById(id, category);
    }

    @Override
    public Category updateCategoryByUuid(UUID uuid, Category category) {
        return repository.updateByUuid(uuid, category);
    }

    @Override
    public void deleteCategory(Long id) {
        repository.delete(id);
    }

    @Override
    public void deleteCategoryByUuid(UUID uuid) {
        repository.deleteByUuid(uuid);
    }
}
