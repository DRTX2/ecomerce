package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.discount.Discount;

import java.util.Optional;

public interface DiscountRepositoryPort {
    Discount save(Discount discount);

    Optional<Discount> findByCode(String code);

    Optional<Discount> findById(Long id);
}
