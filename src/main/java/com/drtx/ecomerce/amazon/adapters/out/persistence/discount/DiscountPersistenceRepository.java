package com.drtx.ecomerce.amazon.adapters.out.persistence.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPersistenceRepository extends JpaRepository<DiscountEntity, Long> {
    Optional<DiscountEntity> findByCode(String code);
}
