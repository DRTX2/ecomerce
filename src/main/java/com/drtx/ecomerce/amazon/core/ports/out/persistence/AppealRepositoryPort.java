package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;

import java.util.Optional;

public interface AppealRepositoryPort {
    Appeal save(Appeal appeal);

    Optional<Appeal> findById(Long id);

    Optional<Appeal> findByIncidenceId(Long incidenceId);
}
