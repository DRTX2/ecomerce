package com.drtx.ecomerce.amazon.adapters.out.persistence.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewPersistenceRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByProductId(Long productId);

    List<ReviewEntity> findByUserId(Long userId);
}
