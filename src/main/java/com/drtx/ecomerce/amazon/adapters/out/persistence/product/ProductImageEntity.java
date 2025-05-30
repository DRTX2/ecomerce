package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String url;

    @ManyToOne
    private ProductEntity product;
}
