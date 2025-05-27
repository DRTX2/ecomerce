package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersistenceRepository extends JpaRepository<OrderEntity,Long> {
}
