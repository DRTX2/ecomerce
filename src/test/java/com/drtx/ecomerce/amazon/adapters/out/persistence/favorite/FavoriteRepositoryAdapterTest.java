package com.drtx.ecomerce.amazon.adapters.out.persistence.favorite;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import jakarta.persistence.EntityManager;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(FavoriteRepositoryAdapter.class)
@DisplayName("Favorite Repository Adapter Integration Tests")
class FavoriteRepositoryAdapterTest {

        @SpringBootConfiguration
        @EnableAutoConfiguration
        @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
        @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
        static class TestConfig {
        }

        @Autowired
        private FavoriteRepositoryAdapter adapter;

        @Autowired
        private FavoritePersistenceRepository favoriteRepository;

        @Autowired
        private UserPersistenceRepository userRepository;

        @Autowired
        private ProductPersistenceRepository productRepository;

        @Autowired
        private CategoryPersistenceRepository categoryRepository;

        @Autowired
        private EntityManager entityManager;

        @MockitoBean
        private FavoritePersistenceMapper mapper;

        @Test
        @DisplayName("Should save a new favorite")
        void testSave() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User");
                user.setEmail("email@test.com");
                user.setUuid(UUID.randomUUID());
                user = userRepository.save(user);

                CategoryEntity cat = new CategoryEntity();
                cat.setName("C");
                cat.setUuid(UUID.randomUUID());
                cat = categoryRepository.save(cat);

                ProductEntity prod = new ProductEntity();
                prod.setName("P");
                prod.setDescription("D");
                prod.setPrice(BigDecimal.ONE);
                prod.setCategory(cat);
                prod.setSku("SKU-FAV");
                prod.setSlug("slug-fav");
                prod.setUuid(UUID.randomUUID());
                prod = productRepository.save(prod);

                Favorite favorite = new Favorite(); // Domain object

                FavoriteEntity entity = new FavoriteEntity();
                entity.setUser(user);
                entity.setProduct(prod);

                when(mapper.toEntity(favorite)).thenReturn(entity);
                when(mapper.toDomain(any(FavoriteEntity.class))).thenAnswer(inv -> {
                        FavoriteEntity e = inv.getArgument(0);
                        Favorite f = new Favorite();
                        f.setId(e.getId());
                        return f;
                });

                // When
                Favorite saved = adapter.save(favorite);

                // Then
                assertThat(saved.getId()).isNotNull();
                assertThat(favoriteRepository.findByUserIdAndProductId(user.getId(), prod.getId())).isPresent();
        }

        @Test
        @DisplayName("Should find favorites by User ID")
        void testFindFavoritesByUserId() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User2");
                user.setEmail("email2@test.com");
                user.setUuid(UUID.randomUUID());
                user = userRepository.save(user);

                CategoryEntity cat = new CategoryEntity();
                cat.setName("C2");
                cat.setUuid(UUID.randomUUID());
                cat = categoryRepository.save(cat);

                ProductEntity prod = new ProductEntity();
                prod.setName("P2");
                prod.setDescription("D");
                prod.setPrice(BigDecimal.ONE);
                prod.setCategory(cat);
                prod.setSku("SKU-FAV2");
                prod.setSlug("slug-fav2");
                prod.setUuid(UUID.randomUUID());
                prod = productRepository.save(prod);

                FavoriteEntity favEntity = new FavoriteEntity();
                favEntity.setUser(user);
                favEntity.setProduct(prod);
                favoriteRepository.save(favEntity);

                Favorite domainFav = new Favorite();
                Product domainProd = new Product();
                domainProd.setId(prod.getId());
                domainProd.setUuid(prod.getUuid());
                domainFav.setProduct(domainProd);

                // Mock list mapping
                when(mapper.toDomainList(anyList())).thenReturn(Collections.singletonList(domainFav));
                when(mapper.toDomain(any(FavoriteEntity.class))).thenReturn(domainFav);

                // When
                List<Favorite> favorites = adapter.findAllByUserId(user.getId());
                List<Product> products = adapter.findFavoritesByUserId(user.getId());

                // Then
                assertThat(favorites).hasSize(1);
                assertThat(products).hasSize(1);
                assertThat(products.get(0).getId()).isEqualTo(prod.getId());
        }

        @Test
        @DisplayName("Should delete favorite by User ID and Product ID")
        void testDelete() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User3");
                user.setEmail("email3@test.com");
                user.setUuid(UUID.randomUUID());
                user = userRepository.save(user);

                CategoryEntity cat = new CategoryEntity();
                cat.setName("C3");
                cat.setUuid(UUID.randomUUID());
                cat = categoryRepository.save(cat);

                ProductEntity prod = new ProductEntity();
                prod.setName("P3");
                prod.setDescription("D");
                prod.setPrice(BigDecimal.ONE);
                prod.setCategory(cat);
                prod.setSku("SKU-FAV3");
                prod.setSlug("slug-fav3");
                prod.setUuid(UUID.randomUUID());
                prod = productRepository.save(prod);

                FavoriteEntity fav = new FavoriteEntity();
                fav.setUser(user);
                fav.setProduct(prod);
                favoriteRepository.save(fav);

                // Flush to ensure persist before delete
                entityManager.flush();
                entityManager.clear();

                // When
                adapter.deleteByUserIdAndProductId(user.getId(), prod.getId());
                entityManager.flush();

                // Then
                assertThat(favoriteRepository.findByUserIdAndProductId(user.getId(), prod.getId())).isEmpty();
        }
}
