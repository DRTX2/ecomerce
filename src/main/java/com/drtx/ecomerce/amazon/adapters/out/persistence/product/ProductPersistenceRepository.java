package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPersistenceRepository extends JpaRepository<ProductEntity, Long> {
}
