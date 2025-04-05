package com.drtx.ecomerce.amazon.core.ports;

import com.drtx.ecomerce.amazon.core.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User updateById(Long id, User user);
    void delete(Long id);
}
