package com.drtx.ecomerce.amazon.adapters.out.persistence.returns;

import com.drtx.ecomerce.amazon.core.model.Return;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ReturnRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReturnRepositoryAdapter implements ReturnRepositoryPort {
    private final ReturnPersistenceRepository repository;
    private final ReturnPersistenceMapper mapper;

    @Override
    public Return save(Return r) {
        ReturnEntity entity = mapper.toEntity(r);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Return> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Return> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomain);
    }
}
