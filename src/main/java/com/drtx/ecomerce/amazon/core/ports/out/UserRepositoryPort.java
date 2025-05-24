package com.drtx.ecomerce.amazon.core.ports.out;

import com.drtx.ecomerce.amazon.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User updateById(Long id, User user);
    void delete(Long id);
}
