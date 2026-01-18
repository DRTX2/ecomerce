package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCasePort {
    Optional<User> getUserByUuid(UUID uuid);
    List<User> getAllUsers();
    User updateUserByUuid(UUID uuid, User user);
    void deleteUserByUuid(UUID uuid);
}
