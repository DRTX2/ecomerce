package com.drtx.ecomerce.amazon.core.model.discount;

import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Discount {
    private Long id;
    private String code;
    private DiscountType type;
    private BigDecimal value;
    private List<Product> applicableProducts;
    private LocalDateTime expirationDate;

    public Discount() {
    }

    public Discount(Long id, String code, DiscountType type, BigDecimal value, List<Product> applicableProducts,
            LocalDateTime expirationDate) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.value = value;
        this.applicableProducts = applicableProducts;
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<Product> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<Product> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
