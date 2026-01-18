package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryPersistenceRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByUuid(UUID uuid);
}