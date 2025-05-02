package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.in.UserServicePort;
import com.drtx.ecomerce.amazon.core.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserUseCasePort implements UserServicePort {
    private final UserRepositoryPort repository;

    public UserUseCasePort(UserRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public User createUser(User user) {
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

    }
}
