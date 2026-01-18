package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartPersistenceRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserId(Long userId);

    Optional<CartEntity> findByUuid(UUID uuid);
}
