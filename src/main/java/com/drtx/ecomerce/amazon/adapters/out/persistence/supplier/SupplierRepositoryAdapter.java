package com.drtx.ecomerce.amazon.adapters.out.persistence.supplier;

import com.drtx.ecomerce.amazon.core.model.Supplier;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepositoryPort {
    private final SupplierPersistenceRepository repository;
    private final SupplierPersistenceMapper mapper;

    @Override
    public Supplier save(Supplier supplier) {
        SupplierEntity entity = mapper.toEntity(supplier);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
