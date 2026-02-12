package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IncidenceRepositoryAdapter implements IncidenceRepositoryPort {

    private final IncidencePersistenceRepository repository;
    private final IncidencePersistenceMapper mapper;

    @Override
    public Incidence save(Incidence incidence) {
        IncidenceEntity entity = mapper.toEntity(incidence);
        // Ensure reports reference the incidence entity for cascade to work if bidirectional
        if (entity.getReports() != null) {
            entity.getReports().forEach(report -> report.setIncidence(entity));
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Incidence> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Incidence> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<Incidence> findByProductIdAndStatusOpen(Long productId) {
        return repository.findByProductIdAndStatus(productId, IncidenceStatus.OPEN)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Incidence> findByProductUuidAndStatusOpen(UUID productUuid) {
        return repository.findByProductUuidAndStatus(productUuid, IncidenceStatus.OPEN)
                .map(mapper::toDomain);
    }

    @Override
    public List<Incidence> findAll() {
        return mapper.toDomainList(repository.findAll());
    }

    @Override
    public Incidence updateById(Long id, Incidence incidence) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Incidence not found with id " + id);
        }
        IncidenceEntity entity = mapper.toEntity(incidence);
        entity.setId(id); // Ensure ID is correct
        if (entity.getReports() != null) {
            entity.getReports().forEach(report -> report.setIncidence(entity));
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Incidence updateByUuid(UUID uuid, Incidence incidence) {
        IncidenceEntity existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Incidence not found with uuid: " + uuid));

        IncidenceEntity entity = mapper.toEntity(incidence);
        entity.setId(existing.getId());
        entity.setUuid(existing.getUuid());

        if (entity.getReports() != null) {
            entity.getReports().forEach(report -> report.setIncidence(entity));
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        IncidenceEntity entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Incidence not found with uuid: " + uuid));
        repository.delete(entity);
    }
}
