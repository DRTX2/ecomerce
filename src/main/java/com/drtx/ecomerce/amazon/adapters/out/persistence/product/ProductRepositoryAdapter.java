package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductPersistenceRepository productPersistenceRepository;
    private final CategoryPersistenceRepository categoryPersistenceRepository;
    private final ProductPersistenceMapper mapper;
    private final ProductMapperHelper mapperHelper;

    @Override
    public Product save(Product product) {
        ProductEntity newEntity = mapper.toEntity(product);
        return mapper.toDomain(productPersistenceRepository.save(newEntity));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productPersistenceRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productPersistenceRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Product updateById(Long id, Product product) {
        ProductEntity productToUpdate = productPersistenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product: " + id));

        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setAverageRating(product.getAverageRating());

        List<ProductImageEntity> images = mapperHelper.mapToEntities(product.getImages());
        productToUpdate.setImages(images);

        CategoryEntity categoryEntity = categoryPersistenceRepository.findById(
                product.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        productToUpdate.setCategory(categoryEntity);// categoryEntity!=Category
        return mapper.toDomain(productPersistenceRepository.save(productToUpdate));
    }

    @Override
    public void delete(Long id) {
        productPersistenceRepository.deleteById(id);
    }
}
