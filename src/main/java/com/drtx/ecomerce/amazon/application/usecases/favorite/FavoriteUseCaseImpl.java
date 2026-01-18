package com.drtx.ecomerce.amazon.application.usecases.favorite;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
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
import java.util.UUID;

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
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));

        if (favoriteRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw DomainExceptionFactory.invalidOperation("Favorite already exists for this product");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(productId));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.initializeDefaults();

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public Favorite addFavoriteByProductUuid(UUID productUuid, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));

        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(productUuid));

        if (favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw DomainExceptionFactory.invalidOperation("Favorite already exists for this product");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.initializeDefaults();

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));

        favoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    @Override
    @Transactional
    public void removeFavoriteByProductUuid(UUID productUuid, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));

        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(productUuid));

        favoriteRepository.deleteByUserIdAndProductId(user.getId(), product.getId());
    }

    @Override
    public List<Product> getUserFavorites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));
        return favoriteRepository.findFavoritesByUserId(user.getId());
    }
}
