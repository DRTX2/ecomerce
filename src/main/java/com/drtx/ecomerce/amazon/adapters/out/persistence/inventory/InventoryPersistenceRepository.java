package com.drtx.ecomerce.amazon.adapters.out.persistence.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryPersistenceRepository extends JpaRepository<InventoryEntity, Long> {
    List<InventoryEntity> findByProductId(Long productId);
}
