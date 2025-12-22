package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.Supplier;

import java.util.Optional;

public interface SupplierRepositoryPort {
    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Long id);
}
