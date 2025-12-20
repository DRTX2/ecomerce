package com.drtx.ecomerce.amazon.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorite {
    private Long id;
    private User user;
    private Product product;
    private LocalDateTime createdAt;

    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
