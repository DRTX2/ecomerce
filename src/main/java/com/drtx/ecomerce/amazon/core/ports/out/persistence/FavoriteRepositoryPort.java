package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepositoryPort {
    Favorite save(Favorite favorite);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    List<Product> findFavoritesByUserId(Long userId); // Returns Products directly or Favorites? Typically Favorites, but UseCase wants Products. 
    // Let's return Favorites to be pure
    List<Favorite> findAllByUserId(Long userId);
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
}
