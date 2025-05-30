package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.core.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentPersistenceRepository extends JpaRepository<PaymentEntity, Long> {
}
