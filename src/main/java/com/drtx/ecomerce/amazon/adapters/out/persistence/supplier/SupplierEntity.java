package com.drtx.ecomerce.amazon.adapters.out.persistence.supplier;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contact;

    @OneToMany
    @JoinTable(name = "supplier_products", joinColumns = @JoinColumn(name = "supplier_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<ProductEntity> suppliedProducts;
}
