package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPersistenceRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUuid(UUID uuid);
    Optional<UserEntity> findByEmail(String email);
}
