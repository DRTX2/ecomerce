package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppealPersistenceRepository extends JpaRepository<AppealEntity, Long> {
    Optional<AppealEntity> findByIncidenceId(Long incidenceId);
}
