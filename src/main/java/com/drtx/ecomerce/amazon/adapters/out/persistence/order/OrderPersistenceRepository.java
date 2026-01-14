package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderPersistenceRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
}
