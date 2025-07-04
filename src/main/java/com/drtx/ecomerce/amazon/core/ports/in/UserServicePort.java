package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServicePort {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
