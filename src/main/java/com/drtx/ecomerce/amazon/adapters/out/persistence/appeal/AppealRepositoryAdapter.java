package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.AppealRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppealRepositoryAdapter implements AppealRepositoryPort {

    private final AppealPersistenceRepository repository;
    private final AppealPersistenceMapper mapper;

    @Override
    public Appeal save(Appeal appeal) {
        return mapper.toDomain(repository.save(mapper.toEntity(appeal)));
    }

    @Override
    public Optional<Appeal> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Appeal> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<Appeal> findByIncidenceId(Long incidenceId) {
        return repository.findByIncidenceId(incidenceId).map(mapper::toDomain);
    }
}
