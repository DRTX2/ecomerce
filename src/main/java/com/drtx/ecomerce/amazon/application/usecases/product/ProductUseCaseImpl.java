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
        repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(id));

        // Business validation
        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0) {
            throw DomainExceptionFactory.invalidProductPrice();
        }

        return repository.updateById(id, product);
    }

    @Override
    public void deleteProduct(Long id) {
        // Verify product exists before deleting
        repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(id));
        repository.delete(id);
    }
}
