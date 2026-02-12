package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidenceRepositoryPort {
    Incidence save(Incidence incidence);

    Optional<Incidence> findById(Long id);
    Optional<Incidence> findByUuid(UUID uuid);

    Optional<Incidence> findByProductIdAndStatusOpen(Long productId);
    Optional<Incidence> findByProductUuidAndStatusOpen(UUID productUuid);
    List<Incidence> findAll();

    Incidence updateById(Long id, Incidence incidence);
    Incidence updateByUuid(UUID uuid, Incidence incidence);

    void delete(Long id);
    void deleteByUuid(UUID uuid);
}
