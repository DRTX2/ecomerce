package com.drtx.ecomerce.amazon.adapters.out.persistence.shipping;

import com.drtx.ecomerce.amazon.core.model.Shipping;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ShippingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShippingRepositoryAdapter implements ShippingRepositoryPort {
    private final ShippingPersistenceRepository repository;
    private final ShippingPersistenceMapper mapper;

    @Override
    public Shipping save(Shipping shipping) {
        ShippingEntity entity = mapper.toEntity(shipping);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Shipping> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Shipping> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomain);
    }
}
