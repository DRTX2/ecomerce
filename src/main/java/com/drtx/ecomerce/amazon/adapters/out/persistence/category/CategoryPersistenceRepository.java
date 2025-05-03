package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryPersistenceRepository  extends JpaRepository<CategoryEntity, Long> {
}