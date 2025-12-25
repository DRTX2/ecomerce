package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(nullable = false)
    private Integer quantity;
}
