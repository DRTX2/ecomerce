package com.drtx.ecomerce.amazon.application.usecases.product;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.pagination.PageResponse;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductUseCaseImpl implements ProductUseCasePort {
    private final ProductRepositoryPort repository;

    @Override
    public Product createProduct(Product product) {
        // Business validation
        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0) {
            throw DomainExceptionFactory.invalidProductPrice();
        }

        // Auto-generate slug if not provided
        if (product.getSlug() == null || product.getSlug().trim().isEmpty()) {
            product.setSlug(generateSlug(product.getName()));
        }

        // Default status
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.DRAFT);
        }

        return repository.save(product);
    }

    @Override
    public Optional<Product> getProductByUuid(UUID id) {
        return repository.findByUuid(id);
    }

    @Override
    public PageResponse<Product> searchProducts(ProductSearchCriteria searchCriteria) {
        Page<Product> page = repository.searchProducts(searchCriteria);

        return PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public List<Product> getPopularProducts(int limit) {
        // Validate limit
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }
        return repository.findPopularProducts(limit);
    }

    @Override
    public List<Product> getDealsProducts(int limit) {
        // Validate limit
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }
        return repository.findDealsProducts(limit);
    }

    @Override
    public Product updateProduct(UUID uuid, Product product) {
        // Verify product exists
        Product existingProduct = repository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(uuid));

        // Business validation
        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0) {
            throw DomainExceptionFactory.invalidProductPrice();
        }

        // Update logic: if slug is missing but name is changing, regenerate slug
        if ((product.getSlug() == null || product.getSlug().isBlank()) && product.getName() != null) {
            product.setSlug(generateSlug(product.getName()));
        }

        return repository.updateByUuid(uuid, product);
    }

    @Override
    public void deleteProductByUuid(UUID uuid) {
        // Verify product exists before deleting
        Product product = repository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(uuid));

        // Soft delete: Change status to ARCHIVED
        product.setStatus(ProductStatus.ARCHIVED);
        repository.updateByUuid(uuid, product);
    }

    // Utility method to generate slug from name
    private String generateSlug(String name) {
        if (name == null)
            return null;
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}