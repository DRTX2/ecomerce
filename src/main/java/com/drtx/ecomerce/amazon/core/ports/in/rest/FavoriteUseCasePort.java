package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.Favorite;
import com.drtx.ecomerce.amazon.core.model.Product;

import java.util.List;

public interface FavoriteUseCasePort {
    Favorite addFavorite(Long productId, String userEmail);
    void removeFavorite(Long productId, String userEmail);
    List<Product> getUserFavorites(String userEmail);
}
