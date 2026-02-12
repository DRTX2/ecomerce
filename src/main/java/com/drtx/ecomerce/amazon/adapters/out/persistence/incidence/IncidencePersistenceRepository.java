package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidencePersistenceRepository extends JpaRepository<IncidenceEntity, Long> {
    Optional<IncidenceEntity> findByProductIdAndStatus(Long productId, IncidenceStatus status);
    Optional<IncidenceEntity> findByProductUuidAndStatus(UUID productUuid, IncidenceStatus status);

    Optional<IncidenceEntity> findByUuid(UUID uuid);
}
