package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderSearchCriteria;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.pagination.PageRequest;
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
import java.util.UUID;

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
                prod.setSku("SKU-ORD");
                prod.setSlug("slug-ord");
                prod.setUuid(UUID.randomUUID());
                prod = productRepository.save(prod);

                Order order = new Order(null, UUID.randomUUID(), null, List.of(), BigDecimal.TEN,
                                OrderState.PENDING, LocalDateTime.now(), null, List.of());

                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setItems(Collections.emptyList());
                entity.setTotal(BigDecimal.TEN);
                entity.setOrderState(OrderState.PENDING);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUuid(order.getUuid());

                when(orderMapper.toEntity(order)).thenReturn(entity);
                when(orderMapper.toDomain(any(OrderEntity.class))).thenAnswer(inv -> {
                        OrderEntity e = inv.getArgument(0);
                        return new Order(e.getId(), e.getUuid(), null, null, null, null, null, null, null);
                });

                // When
                Order savedOrder = adapter.save(order);

                // Then
                assertThat(savedOrder.getId()).isNotNull();
                assertThat(savedOrder.getUuid()).isEqualTo(order.getUuid());
        }

        @Test
        @DisplayName("Should find order by ID")
        void testFindById() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User2");
                user.setEmail("email2@test.com");
                user.setUuid(UUID.randomUUID());
                user.setEnabled(true);
                user.setLocked(false);
                user = userRepository.save(user);

                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setTotal(BigDecimal.TEN);
                entity.setOrderState(OrderState.PENDING);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setItems(Collections.emptyList());
                entity.setUuid(UUID.randomUUID());
                entity = orderRepository.save(entity);

                Order domain = new Order(entity.getId(), entity.getUuid(), null, null, null, null, null, null, List.of());

                when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(domain);

                // When
                Optional<Order> found = adapter.findById(entity.getId());

                // Then
                assertThat(found).isPresent();
        }

        @Test
        @DisplayName("Should find all orders with criteria")
        void testFindAll() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User3");
                user.setEmail("email3@test.com");
                user.setUuid(UUID.randomUUID());
                user.setEnabled(true);
                user.setLocked(false);
                user = userRepository.save(user);

                OrderEntity order1 = new OrderEntity();
                order1.setUser(user);
                order1.setOrderState(OrderState.PENDING);
                order1.setUuid(UUID.randomUUID());
                order1.setTotal(BigDecimal.ONE);
                order1.setCreatedAt(LocalDateTime.now());
                orderRepository.save(order1);

                OrderEntity order2 = new OrderEntity();
                order2.setUser(user);
                order2.setOrderState(OrderState.SENT);
                order2.setUuid(UUID.randomUUID());
                order2.setTotal(BigDecimal.ONE);
                order2.setCreatedAt(LocalDateTime.now());
                orderRepository.save(order2);

                OrderSearchCriteria criteria = new OrderSearchCriteria(
                        Optional.empty(),
                        Optional.empty(),
                        null, null, null, null,
                        PageRequest.of(0, 10)
                );

                when(orderMapper.toDomain(any(OrderEntity.class)))
                                .thenReturn(new Order(null, UUID.randomUUID(), null, List.of(), BigDecimal.ONE, OrderState.PENDING, null, null, List.of()));

                // When
                org.springframework.data.domain.Page<Order> all = adapter.searchOrders(criteria);

                // Then
                assertThat(all.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Should update existing order")
        void testUpdateById() {
                // Given
                UserEntity user = new UserEntity();
                user.setName("User4");
                user.setEmail("email4@test.com");
                user.setUuid(UUID.randomUUID());
                user.setEnabled(true);
                user.setLocked(false);
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
                prod.setSku("SKU-ORD2");
                prod.setSlug("slug-ord2");
                prod.setUuid(UUID.randomUUID());
                prod.setStockQuantity(100);
                prod.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
                prod = productRepository.save(prod);

                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setOrderState(OrderState.PENDING);
                entity.setUuid(UUID.randomUUID());
                entity.setTotal(BigDecimal.ONE);
                entity.setCreatedAt(LocalDateTime.now());
                entity = orderRepository.save(entity);

                Order updateData = new Order(entity.getId(), entity.getUuid(), null, List.of(),
                                BigDecimal.valueOf(20), OrderState.SENT, null, null, List.of());

                when(orderMapper.toDomain(any(OrderEntity.class))).thenAnswer(inv -> {
                        OrderEntity e = inv.getArgument(0);
                        return new Order(e.getId(), e.getUuid(), null, List.of(), e.getTotal(), e.getOrderState(), null, null, List.of());
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
                UserEntity user = new UserEntity();
                user.setName("User5");
                user.setEmail("email5@test.com");
                user.setUuid(UUID.randomUUID());
                user.setEnabled(true);
                user.setLocked(false);
                user = userRepository.save(user);

                OrderEntity entity = new OrderEntity();
                entity.setUser(user);
                entity.setOrderState(OrderState.PENDING);
                entity.setUuid(UUID.randomUUID());
                entity.setTotal(BigDecimal.ONE);
                entity.setCreatedAt(LocalDateTime.now());
                entity = orderRepository.save(entity);

                // When
                adapter.delete(entity.getId());

                // Then
                assertThat(orderRepository.existsById(entity.getId())).isFalse();
        }
}
