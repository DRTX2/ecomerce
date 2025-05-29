package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartPersistenceRepository extends JpaRepository<CartEntity,Long> {
}
