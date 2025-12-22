package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartPersistenceRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserId(Long userId);
}
