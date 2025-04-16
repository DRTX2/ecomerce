package com.drtx.ecomerce.amazon.application.services;

import com.drtx.ecomerce.amazon.core.models.User;
import com.drtx.ecomerce.amazon.core.ports.out.UserRepository;
import com.drtx.ecomerce.amazon.core.ports.in.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
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
