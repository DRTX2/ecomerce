package com.drtx.ecomerce.amazon.adapters.out.persistence.inventory;

import com.drtx.ecomerce.amazon.core.model.Inventory;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {
    private final InventoryPersistenceRepository repository;
    private final InventoryPersistenceMapper mapper;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = mapper.toEntity(inventory);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Inventory> findByProductId(Long productId) {
        return mapper.toDomainList(repository.findByProductId(productId));
    }
}
