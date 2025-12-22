package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.Cart;
import com.drtx.ecomerce.amazon.core.model.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(CartRepositoryAdapter.class)
@DisplayName("Cart Repository Adapter Integration Tests")
class CartRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private CartRepositoryAdapter adapter;

    @Autowired
    private CartPersistenceRepository cartRepository;

    @Autowired
    private UserPersistenceRepository userRepository;

    @Autowired
    private ProductPersistenceRepository productRepository;

    @Autowired
    private CategoryPersistenceRepository categoryRepository;

    @MockitoBean
    private CartPersistenceMapper mapper;

    @Test
    @DisplayName("Should save a new cart")
    void testSave() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("cartuser@example.com");
        userEntity = userRepository.save(userEntity);

        CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "C", null, null));
        ProductEntity prod = productRepository
                .save(new ProductEntity(null, "P", "D", BigDecimal.ONE, 1, cat, BigDecimal.ONE, null));

        Cart cart = new Cart();

        CartEntity entity = new CartEntity();
        entity.setUser(userEntity);
        // Important: Cart entity cascade logic.
        // If we set prod in CartEntity, ID must be managed.
        entity.setProducts(Collections.singletonList(prod));

        when(mapper.toEntity(cart)).thenReturn(entity);
        when(mapper.toDomain(any(CartEntity.class))).thenAnswer(inv -> {
            CartEntity e = inv.getArgument(0);
            Cart c = new Cart();
            c.setId(e.getId());
            return c;
        });

        // When
        Cart savedCart = adapter.save(cart);

        // Then
        assertThat(savedCart.getId()).isNotNull();
        CartEntity fromDb = cartRepository.findById(savedCart.getId()).orElseThrow();
        assertThat(fromDb.getUser().getEmail()).isEqualTo("cartuser@example.com");
        assertThat(fromDb.getProducts()).hasSize(1);
    }

    @Test
    @DisplayName("Should find cart by User ID")
    void testFindByUserId() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("find@example.com");
        userEntity = userRepository.save(userEntity);

        CartEntity entity = new CartEntity();
        entity.setUser(userEntity);
        entity = cartRepository.save(entity);

        Cart domainCart = new Cart();
        domainCart.setId(entity.getId());

        when(mapper.toDomain(any(CartEntity.class))).thenReturn(domainCart);

        // When
        List<Cart> carts = adapter.findAll(userEntity.getId());

        // Then
        assertThat(carts).hasSize(1);
    }

    @Test
    @DisplayName("Should delete cart")
    void testDelete() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("del@example.com");
        userEntity = userRepository.save(userEntity);

        CartEntity entity = new CartEntity();
        entity.setUser(userEntity);
        entity = cartRepository.save(entity);

        // When
        adapter.delete(entity.getId());

        // Then
        assertThat(cartRepository.existsById(entity.getId())).isFalse();
    }
}
