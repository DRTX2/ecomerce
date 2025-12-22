package com.drtx.ecomerce.amazon.adapters.out.persistence.discount;

import com.drtx.ecomerce.amazon.core.model.Discount;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.DiscountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DiscountRepositoryAdapter implements DiscountRepositoryPort {
    private final DiscountPersistenceRepository repository;
    private final DiscountPersistenceMapper mapper;

    @Override
    public Discount save(Discount discount) {
        DiscountEntity entity = mapper.toEntity(discount);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Discount> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Optional<Discount> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
