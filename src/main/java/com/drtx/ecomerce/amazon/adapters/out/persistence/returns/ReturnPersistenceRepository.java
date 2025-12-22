package com.drtx.ecomerce.amazon.adapters.out.persistence.returns;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnPersistenceRepository extends JpaRepository<ReturnEntity, Long> {
    Optional<ReturnEntity> findByOrderId(Long orderId);
}
