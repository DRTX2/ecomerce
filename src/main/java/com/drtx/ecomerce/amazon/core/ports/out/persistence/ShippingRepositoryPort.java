package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.Shipping;

import java.util.Optional;

public interface ShippingRepositoryPort {
    Shipping save(Shipping shipping);

    Optional<Shipping> findById(Long id);

    Optional<Shipping> findByOrderId(Long orderId);
}
