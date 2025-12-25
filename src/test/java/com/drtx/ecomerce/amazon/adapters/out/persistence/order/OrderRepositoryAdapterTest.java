package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.product.Product;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(OrderRepositoryAdapter.class)
@DisplayName("Order Repository Adapter Integration Tests")
class OrderRepositoryAdapterTest {

        @SpringBootConfiguration
        @EnableAutoConfiguration
        @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
        @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
        static class TestConfig {
        }

        @Autowired
        private OrderRepositoryAdapter adapter;

        @Autowired
        private OrderPersistenceRepository orderRepository;

        @Autowired
        private UserPersistenceRepository userRepository;

        @Autowired
        private ProductPersistenceRepository productRepository;

        @Autowired
        private CategoryPersistenceRepository categoryRepository;

        @MockitoBean
        private OrderPersistenceMapper orderMapper;

        @MockitoBean
        private ProductPersistenceMapper productMapper;

        @Test
        @DisplayName("Should save a new order")
        void testSave() {
                // Given
                UserEntity user = userRepository
                                .save(new UserEntity(null, "User", "email@test.com", "pass", "addr", "123", null));
                CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "C", null, null));
                ProductEntity prod = productRepository
                                .save(new ProductEntity(null, "P", "D", BigDecimal.ONE, cat, BigDecimal.ONE, null));

                Order order = new Order(null, null, List.of(), BigDecimal.TEN,
                                OrderState.PENDING, LocalDateTime.now(), null, List.of());

                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setItems(Collections.emptyList());
                entity.setTotal(BigDecimal.TEN);
                entity.setOrderState(OrderState.PENDING);
                entity.setCreatedAt(LocalDateTime.now());

                when(orderMapper.toEntity(order)).thenReturn(entity);
                when(orderMapper.toDomain(any(OrderEntity.class))).thenAnswer(inv -> {
                        OrderEntity e = inv.getArgument(0);
                        return new Order(e.getId(), null, null, null, null, null, null, null);
                });

                // When
                Order savedOrder = adapter.save(order);

                // Then
                assertThat(savedOrder.getId()).isNotNull();
                OrderEntity fromDb = orderRepository.findById(savedOrder.getId()).orElseThrow();
                assertThat(fromDb.getUser().getId()).isEqualTo(user.getId());
                assertThat(fromDb.getItems()).isEmpty();
        }

        @Test
        @DisplayName("Should find order by ID")
        void testFindById() {
                // Given
                UserEntity user = userRepository
                                .save(new UserEntity(null, "User2", "email2@test.com", "pass", "addr", "123", null));
                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setTotal(BigDecimal.TEN);
                entity.setOrderState(OrderState.PENDING);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setItems(Collections.emptyList());
                entity = orderRepository.save(entity);

                Order domain = new Order(entity.getId(), null, null, null, null, null, null, List.of());

                when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(domain);

                // When
                Optional<Order> found = adapter.findById(entity.getId());

                // Then
                assertThat(found).isPresent();
        }

        @Test
        @DisplayName("Should find all orders")
        void testFindAll() {
                // Given
                UserEntity user = userRepository
                                .save(new UserEntity(null, "User3", "email3@test.com", "pass", "addr", "123", null));
                orderRepository.save(new OrderEntity(null, user, Collections.emptyList(), Collections.emptyList(),
                                BigDecimal.ONE,
                                OrderState.PENDING, LocalDateTime.now(), null));
                orderRepository.save(new OrderEntity(null, user, Collections.emptyList(), Collections.emptyList(),
                                BigDecimal.ONE,
                                OrderState.SENT, LocalDateTime.now(), null));

                when(orderMapper.toDomain(any(OrderEntity.class)))
                                .thenReturn(new Order(null, null, null, null, null, null, null, List.of()));

                // When
                List<Order> all = adapter.findAll();

                // Then
                assertThat(all).hasSize(2);
        }

        @Test
        @DisplayName("Should update existing order")
        void testUpdateById() {
                // Given
                UserEntity user = userRepository
                                .save(new UserEntity(null, "User4", "email4@test.com", "pass", "addr", "123", null));
                CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "C2", null, null));
                ProductEntity prod = productRepository
                                .save(new ProductEntity(null, "P2", "D", BigDecimal.ONE, cat, BigDecimal.ONE, null));

                OrderEntity entity = orderRepository.save(new OrderEntity(null, user, Collections.emptyList(),
                                Collections.emptyList(), BigDecimal.ONE, OrderState.PENDING, LocalDateTime.now(),
                                null));

                Product domainProduct = new Product();
                domainProduct.setId(prod.getId());

                Order updateData = new Order(entity.getId(), null, List.of(),
                                BigDecimal.valueOf(20), OrderState.SENT, null, null, List.of());

                when(productMapper.toEntity(any(Product.class))).thenReturn(prod);
                when(orderMapper.toDomain(any(OrderEntity.class))).thenAnswer(inv -> {
                        OrderEntity e = inv.getArgument(0);
                        return new Order(e.getId(), null, null, null, e.getOrderState(), null, null, List.of());
                });

                // When
                Order updated = adapter.updateById(updateData);

                // Then
                assertThat(updated.getOrderState()).isEqualTo(OrderState.SENT);
                OrderEntity fromDb = orderRepository.findById(entity.getId()).orElseThrow();
                assertThat(fromDb.getOrderState()).isEqualTo(OrderState.SENT);
        }

        @Test
        @DisplayName("Should delete order")
        void testDelete() {
                // Given
                UserEntity user = userRepository
                                .save(new UserEntity(null, "User5", "email5@test.com", "pass", "addr", "123", null));
                OrderEntity entity = orderRepository.save(new OrderEntity(null, user, Collections.emptyList(),
                                Collections.emptyList(), BigDecimal.ONE, OrderState.PENDING, LocalDateTime.now(),
                                null));

                // When
                adapter.delete(entity.getId());

                // Then
                assertThat(orderRepository.existsById(entity.getId())).isFalse();
        }
}
