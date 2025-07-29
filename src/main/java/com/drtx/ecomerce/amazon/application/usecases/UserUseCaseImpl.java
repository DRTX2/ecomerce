package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public User createUser(User user) {
        String encodedPassword=passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return repository.save(user);
    }

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
        return repository.updateById(id, user);
    }

    @Override
    public void deleteUser(Long id) {
        repository.delete(id);
    }
}
