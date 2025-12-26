package com.drtx.ecomerce.amazon.adapters.out.persistence.notification;

import com.drtx.ecomerce.amazon.core.model.notifications.Notification;
import com.drtx.ecomerce.amazon.core.model.notifications.NotificationStatus;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.NotificationRepositoryPort;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
    private final NotificationPersistenceRepository repository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return mapper.toDomainList(repository.findByUserId(userId));
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void markAsRead(Long id) {
        NotificationEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        entity.setStatus(NotificationStatus.READ);
        repository.save(entity);
    }
}
