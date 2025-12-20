package com.drtx.ecomerce.amazon.adapters.out.persistence.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePersistenceRepository extends JpaRepository<FavoriteEntity, Long> {
    void deleteByUserIdAndProductId(Long userId, Long productId);
    List<FavoriteEntity> findAllByUserId(Long userId);
    Optional<FavoriteEntity> findByUserIdAndProductId(Long userId, Long productId);
}
