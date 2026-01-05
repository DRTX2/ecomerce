package com.drtx.ecomerce.amazon.application.usecases;
import com.drtx.ecomerce.amazon.application.usecases.favorite.FavoriteUseCaseImpl;

import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.FavoriteRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteUseCaseImplTest {

    @Mock
    private FavoriteRepositoryPort favoriteRepository;
    @Mock
    private ProductRepositoryPort productRepository;
    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private FavoriteUseCaseImpl favoriteUseCase;

    @Test
    void addFavorite_ShouldSucceed_WhenNotDuplicate() {
        // Arrange
        String userEmail = "user@test.com";
        Long productId = 5L;
        User user = new User(1L, "Test User", userEmail, "password", "address", "phone", null);
        
        Product product = new Product();
        product.setId(productId);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Favorite result = favoriteUseCase.addFavorite(productId, userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_ShouldThrow_WhenDuplicate() {
        // Arrange
        String userEmail = "user@test.com";
        Long productId = 5L;
        User user = new User(1L, "Test User", userEmail, "password", "address", "phone", null);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(Optional.of(new Favorite()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> favoriteUseCase.addFavorite(productId, userEmail));
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void getUserFavorites_ShouldReturnList() {
        // Arrange
        String userEmail = "user@test.com";
        User user = new User(1L, "Test User", userEmail, "password", "address", "phone", null);
        
        List<Product> mockProducts = List.of(new Product(), new Product());

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(favoriteRepository.findFavoritesByUserId(user.getId())).thenReturn(mockProducts);

        // Act
        List<Product> result = favoriteUseCase.getUserFavorites(userEmail);

        // Assert
        assertEquals(2, result.size());
        verify(favoriteRepository).findFavoritesByUserId(user.getId());
    }
}
