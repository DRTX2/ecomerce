package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.core.model.OrderState;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToMany
    @JoinTable( //intermediate table
            name = "order_products",
            joinColumns = @JoinColumn(name="order_id"),//column that map this entity(foreign key)
            inverseJoinColumns = @JoinColumn(name = "product_id")//columdn that points to the another entity(products)
    )
    private List<ProductEntity> products;

    @Column(precision = 19, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState orderState;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    @Column(nullable = false)
    private String paymentType;
}
