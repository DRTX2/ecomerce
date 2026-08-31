package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;

public interface ProductPersistenceRepository extends JpaRepository<ProductEntity, Long> {
    @Query("""
            select product from ProductEntity product
             where product.status = :status
               and (:query is null or lower(product.name) like lower(concat('%', :query, '%')))
               and (:categoryId is null or product.category.id = :categoryId)
             order by product.createdAt desc, product.id desc
            """)
    Page<ProductEntity> searchByActiveCatalog(
            @Param("status") ProductStatus status,
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update ProductEntity product
               set product.stockQuantity = product.stockQuantity - :quantity
             where product.id = :productId
               and product.stockQuantity >= :quantity
            """)
    int reserveStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
