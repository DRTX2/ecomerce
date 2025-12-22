package com.drtx.ecomerce.amazon.adapters.out.persistence.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingPersistenceRepository extends JpaRepository<ShippingEntity, Long> {
    Optional<ShippingEntity> findByOrderId(Long orderId);
}
