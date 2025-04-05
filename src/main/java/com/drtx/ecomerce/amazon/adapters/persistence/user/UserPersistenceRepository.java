package com.drtx.ecomerce.amazon.adapters.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPersistenceRepository extends JpaRepository<UserEntity,Long> {
}
