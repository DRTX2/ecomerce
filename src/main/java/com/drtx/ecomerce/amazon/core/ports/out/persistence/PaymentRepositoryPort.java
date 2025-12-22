package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.Payment;

import java.util.Optional;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Payment update(Payment payment);
}
