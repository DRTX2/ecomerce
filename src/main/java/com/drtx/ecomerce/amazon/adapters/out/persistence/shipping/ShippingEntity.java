package com.drtx.ecomerce.amazon.adapters.out.persistence.shipping;

import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderEntity;
import com.drtx.ecomerce.amazon.core.model.shipping.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status;

    private String carrier;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ShippingStatus.PREPARING;
        }
    }
}
