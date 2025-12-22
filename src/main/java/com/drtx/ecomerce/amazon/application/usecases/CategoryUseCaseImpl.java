package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.Category;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CategoryRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryUseCaseImpl implements com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort {
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
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return repository.updateById(id, category);
    }

    @Override
    public void deleteCategory(Long id) {
        repository.delete(id);
    }
}
