package com.drtx.ecomerce.amazon.adapters.out.persistence.review;

import com.drtx.ecomerce.amazon.core.model.product.Review;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ReviewRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepositoryPort {
    private final ReviewPersistenceRepository repository;
    private final ReviewPersistenceMapper mapper;

    @Override
    public Review save(Review review) {
        ReviewEntity entity = mapper.toEntity(review);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Review> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return mapper.toDomainList(repository.findByProductId(productId));
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return mapper.toDomainList(repository.findByUserId(userId));
    }
}
