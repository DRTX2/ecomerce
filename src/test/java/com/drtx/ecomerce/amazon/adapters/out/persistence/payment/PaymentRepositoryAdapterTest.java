package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.payment.Payment;
import com.drtx.ecomerce.amazon.core.model.payment.PaymentMethod;
import com.drtx.ecomerce.amazon.core.model.payment.PaymentStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(PaymentRepositoryAdapter.class)
@DisplayName("Payment Repository Adapter Integration Tests")
class PaymentRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private PaymentRepositoryAdapter adapter;

    @Autowired
    private PaymentPersistenceRepository paymentRepository;

    @Autowired
    private OrderPersistenceRepository orderRepository;

    @Autowired
    private UserPersistenceRepository userRepository;

    @MockitoBean
    private PaymentPersistenceMapper mapper;

    @Test
    @DisplayName("Should save payment")
    void testSave() {
        // Given
        UserEntity user = userRepository.save(new UserEntity(null, "UserP", "p@mail.com", "pass", "addr", "1", null));
        OrderEntity order = new OrderEntity(null, user, Collections.emptyList(), Collections.emptyList(),
                BigDecimal.TEN, OrderState.PENDING,
                LocalDateTime.now(), null);
        order = orderRepository.save(order);

        Payment payment = new Payment(null, null, BigDecimal.TEN, PaymentStatus.PENDING, PaymentMethod.CREDIT_CARD,
                LocalDateTime.now());

        PaymentEntity entity = new PaymentEntity();
        entity.setOrder(order);
        entity.setAmount(BigDecimal.TEN);
        entity.setStatus(PaymentStatus.PENDING);
        entity.setMethod(PaymentMethod.CREDIT_CARD);
        entity.setPaymentDate(LocalDateTime.now());

        when(mapper.toEntity(payment)).thenReturn(entity);
        when(mapper.toDomain(any(PaymentEntity.class))).thenAnswer(inv -> {
            PaymentEntity e = inv.getArgument(0);
            return new Payment(e.getId(), null, e.getAmount(), e.getStatus(), e.getMethod(), e.getPaymentDate());
        });

        // When
        Payment saved = adapter.save(payment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(paymentRepository.findById(saved.getId())).isPresent();
    }
}
