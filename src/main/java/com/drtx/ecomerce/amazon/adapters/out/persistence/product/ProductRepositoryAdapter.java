package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductPage;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;

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
    public ProductPage searchActive(String query, Long categoryId, int page, int size) {
        var result = productPersistenceRepository.searchByActiveCatalog(ProductStatus.ACTIVE, query, categoryId,
                PageRequest.of(page, size));
        return new ProductPage(result.getContent().stream().map(mapper::toDomain).toList(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public Product updateById(Long id, Product product) {
        ProductEntity productToUpdate = productPersistenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product: " + id));

        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setAverageRating(product.getAverageRating());
        if (product.getSku() != null) {
            productToUpdate.setSku(product.getSku());
        }
        if (product.getStockQuantity() != null) {
            productToUpdate.setStockQuantity(product.getStockQuantity());
        }
        if (product.getStatus() != null) {
            productToUpdate.setStatus(product.getStatus());
        }
        if (product.getSlug() != null) {
            productToUpdate.setSlug(product.getSlug());
        }

        List<ProductImageEntity> images = mapperHelper.mapToEntities(product.getImages());
        productToUpdate.setImages(images);

        CategoryEntity categoryEntity = categoryPersistenceRepository.findById(
                product.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        productToUpdate.setCategory(categoryEntity);// categoryEntity!=Category
        return mapper.toDomain(productPersistenceRepository.save(productToUpdate));
    }

    @Override
    public boolean reserveStock(Long productId, int quantity) {
        return productPersistenceRepository.reserveStock(productId, quantity) == 1;
    }

    @Override
    public void delete(Long id) {
        productPersistenceRepository.deleteById(id);
    }
}
