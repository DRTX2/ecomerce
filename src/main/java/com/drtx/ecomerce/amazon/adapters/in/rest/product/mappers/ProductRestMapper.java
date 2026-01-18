package com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductSearchRequest;
import com.drtx.ecomerce.amazon.core.model.pagination.PageRequest;
import com.drtx.ecomerce.amazon.core.model.pagination.SortDirection;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ProductRestMapper {
    Product toDomain(ProductRequest request);

    ProductResponse toResponse(Product domain);

    /**
     * Map ProductSearchRequest to ProductSearchCriteria
     */
    default ProductSearchCriteria toCriteria(ProductSearchRequest request) {
        // Build PageRequest
        PageRequest.Sort sort = new PageRequest.Sort(
                request.sortBy(),
                "ASC".equalsIgnoreCase(request.sortDirection())
                        ? SortDirection.ASC
                        : SortDirection.DESC
        );

        PageRequest pageRequest = new PageRequest(
                request.page(),
                request.size(),
                Optional.of(sort)
        );

        // Build ProductSearchCriteria using builder
        return ProductSearchCriteria.builder()
                .searchTerm(request.searchTerm())
                .categoryUuid(request.categoryUuid())
                .status(request.status())
                .priceRange(request.minPrice(), request.maxPrice())
                .ratingRange(request.minRating(), request.maxRating())
                .onSale(request.onSale())
                .inStock(request.inStock())
                .featuredOnly(request.featuredOnly())
                .pageRequest(pageRequest)
                .build();
    }
}

