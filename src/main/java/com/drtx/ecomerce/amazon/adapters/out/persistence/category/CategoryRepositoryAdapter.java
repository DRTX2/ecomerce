package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CategoryRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryPersistenceRepository repository;
    private final CategoryPersistenceMapper mapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity savedEntity = repository.save(entity);
        return mapper.toDomain(
                savedEntity
        );
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().
                stream().
                map(mapper::toDomain).
                collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Category updateById(Long id, Category category) {
        CategoryEntity entityToUpdate = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category: " + id));
        entityToUpdate.setName(category.getName());
        entityToUpdate.setDescription(category.getDescription());
        return mapper.toDomain(
                repository.save(entityToUpdate)
        );
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
