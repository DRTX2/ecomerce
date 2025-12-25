package com.drtx.ecomerce.amazon.adapters.out.persistence.favorite;

import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.FavoriteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteRepositoryAdapter implements FavoriteRepositoryPort {

    private final FavoritePersistenceRepository repository;
    private final FavoritePersistenceMapper mapper;

    @Override
    public Favorite save(Favorite favorite) {
        return mapper.toDomain(repository.save(mapper.toEntity(favorite)));
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        repository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<Product> findFavoritesByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(entity -> mapper.toDomain(entity).getProduct())
                .collect(Collectors.toList());
    }

    @Override
    public List<Favorite> findAllByUserId(Long userId) {
        return mapper.toDomainList(repository.findAllByUserId(userId));
    }

    @Override
    public Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId) {
        return repository.findByUserIdAndProductId(userId, productId).map(mapper::toDomain);
    }
}
