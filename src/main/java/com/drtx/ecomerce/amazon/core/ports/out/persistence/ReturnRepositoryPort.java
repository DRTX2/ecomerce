package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.Return;

import java.util.Optional;

public interface ReturnRepositoryPort {
    Return save(Return r);

    Optional<Return> findById(Long id);

    Optional<Return> findByOrderId(Long orderId);
}
