package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.core.model.Payment;
import com.drtx.ecomerce.amazon.core.ports.out.PaymentRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {
    private final PaymentPersistenceRepository repository;
    private final PaymentMapper mapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = mapper.toEntity(payment);
        entity=repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAll(Long userId) {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
