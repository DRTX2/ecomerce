package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;

import java.util.List;
import java.util.Optional;

public interface IncidenceRepositoryPort {
    Incidence save(Incidence incidence);
    Optional<Incidence> findById(Long id);
    Optional<Incidence> findByProductIdAndStatusOpen(Long productId);
    List<Incidence> findAll();
    Incidence updateById(Long id, Incidence incidence);
    void delete(Long id);
}
