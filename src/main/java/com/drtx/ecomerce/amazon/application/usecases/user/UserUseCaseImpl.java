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

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCasePort {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;

    @Override
    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        // Verify user exists
        repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(id));

        return repository.updateById(id, user);
    }

    @Override
    public void deleteUser(Long id) {
        // Verify user exists before deleting
        repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(id));

        repository.delete(id);
    }
}
