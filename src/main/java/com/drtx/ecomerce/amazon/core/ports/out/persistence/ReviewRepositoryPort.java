package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryPort {
    Review save(Review review);
    Optional<Review> findById(Long id);
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
}
