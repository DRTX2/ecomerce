package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.core.model.payment.Payment;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.PaymentRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentPersistenceRepository repository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = mapper.toEntity(payment);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Payment update(Payment payment) {
        if (!repository.existsById(payment.getId())) {
            throw new EntityNotFoundException("Payment not found with id: " + payment.getId());
        }
        PaymentEntity entity = mapper.toEntity(payment);
        return mapper.toDomain(repository.save(entity));
    }
}
