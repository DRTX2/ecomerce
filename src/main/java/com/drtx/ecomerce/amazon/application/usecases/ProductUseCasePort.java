package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductUseCasePort implements com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort {
    private final ProductRepositoryPort repository;

    @Override
    public Product createProduct(Product product) {
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
        return repository.updateById(product.getId(),product);
    }

    @Override
    public void deleteProduct(Long id) {
        repository.delete(id);
    }
}
