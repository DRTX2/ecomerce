package com.drtx.ecomerce.amazon.core.model.product;

public enum ProductStatus {
    DRAFT, // borrador, product while is being created and not yet available for sale
    ACTIVE,
    ARCHIVED,
    OUT_OF_STOCK // product is active but currently out of stock, this status can be used to prevent customers from purchasing the product while still keeping it visible on the storefront
}
