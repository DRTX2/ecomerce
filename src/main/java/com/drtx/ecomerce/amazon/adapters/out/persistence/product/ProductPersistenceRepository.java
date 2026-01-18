package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductPersistenceRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
}
