package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long id);

    List<Inventory> findByProductId(Long productId);
}
