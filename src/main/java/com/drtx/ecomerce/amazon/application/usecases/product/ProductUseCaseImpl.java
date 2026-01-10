package com.drtx.ecomerce.amazon.application.usecases.product;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductUseCaseImpl implements com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort {
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
            product.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.DRAFT);
        }

        return repository.save(product);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        // Verify product exists
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(id));

        // Business validation
        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0) {
            throw DomainExceptionFactory.invalidProductPrice();
        }

        // Update logic: if slug is missing but name is changing, regenerate slug
        if ((product.getSlug() == null || product.getSlug().isBlank()) && product.getName() != null) {
            product.setSlug(generateSlug(product.getName()));
        }

        return repository.updateById(id, product);
    }

    @Override
    public void deleteProduct(Long id) {
        // Verify product exists before deleting
        Product product = repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(id));

        // Soft delete: Change status to ARCHIVED
        product.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ARCHIVED);
        repository.updateById(id, product);
    }

    private String generateSlug(String name) {
        if (name == null)
            return null;
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
