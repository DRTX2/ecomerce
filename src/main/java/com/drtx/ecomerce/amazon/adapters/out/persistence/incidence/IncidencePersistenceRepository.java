package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.core.model.IncidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncidencePersistenceRepository extends JpaRepository<IncidenceEntity, Long> {
    Optional<IncidenceEntity> findByProductIdAndStatus(Long productId, IncidenceStatus status);
}
