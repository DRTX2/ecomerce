package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentServicePort
{
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getAllPayments(Long userId);
}
