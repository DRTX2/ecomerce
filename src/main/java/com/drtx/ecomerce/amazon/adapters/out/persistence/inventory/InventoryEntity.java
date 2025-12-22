package com.drtx.ecomerce.amazon.adapters.out.persistence.inventory;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer availableQuantity;

    private String warehouseLocation;
}
