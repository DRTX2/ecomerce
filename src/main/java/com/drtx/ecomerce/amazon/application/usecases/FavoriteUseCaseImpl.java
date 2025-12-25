package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.FavoriteUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.FavoriteRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteUseCaseImpl implements FavoriteUseCasePort {

    private final FavoriteRepositoryPort favoriteRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public Favorite addFavorite(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already exists
        if (favoriteRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new RuntimeException("Favorite already exists");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();
        favorite.initializeDefaults();

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId, String userEmail) {
         User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
         
         favoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    @Override
    public List<Product> getUserFavorites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return favoriteRepository.findFavoritesByUserId(user.getId());
    }
}
