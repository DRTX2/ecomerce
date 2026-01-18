package com.drtx.ecomerce.amazon.application.usecases.user;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCasePort {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;

    @Override
    public Optional<User> getUserByUuid(UUID uuid) {
        return repository.findByUuid(uuid);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User updateUserByUuid(UUID uuid, User user) {
        // Verify user exists
        repository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(uuid));

        return repository.updateByUuid(uuid, user);
    }

    @Override
    public void deleteUserByUuid(UUID uuid) {
        // Verify user exists before deleting
        repository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(uuid));

        repository.deleteByUuid(uuid);
    }
}
