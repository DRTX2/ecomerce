package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.util.List;
import java.util.UUID;

public interface FavoriteUseCasePort {
    Favorite addFavorite(Long productId, String userEmail);
    Favorite addFavoriteByProductUuid(UUID productUuid, String userEmail);

    void removeFavorite(Long productId, String userEmail);
    void removeFavoriteByProductUuid(UUID productUuid, String userEmail);

    List<Product> getUserFavorites(String userEmail);
}
