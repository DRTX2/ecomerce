package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.application.usecases.product.ProductUseCaseImpl;
import com.drtx.ecomerce.amazon.core.model.pagination.PageResponse;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUseCaseImpl Unit Tests")
class ProductUseCaseImplTest {

    private static final LocalDateTime NOW =
            LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    private Product testProduct;
    private Category testCategory;
    private UUID productUuid;

    @BeforeEach
    void setUp() {
        productUuid = UUID.randomUUID();

        testCategory = new Category(
                1L,
                UUID.randomUUID(),
                "Electronics",
                "Electronic devices",
                List.of()
        );

        testProduct = new Product(
                1L,
                productUuid,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                testCategory,
                new BigDecimal("4.5"),
                List.of("image1.jpg", "image2.jpg"),
                "LAPTOP-001",
                50,
                ProductStatus.ACTIVE,
                "laptop",
                NOW,
                NOW
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        when(productRepositoryPort.save(any(Product.class)))
                .thenReturn(testProduct);

        Product result = productUseCase.createProduct(testProduct);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(productUuid);
        verify(productRepositoryPort).save(testProduct);
    }

    @Test
    @DisplayName("Should get product by UUID successfully")
    void shouldGetProductByUuidSuccessfully() {
        when(productRepositoryPort.findByUuid(productUuid))
                .thenReturn(Optional.of(testProduct));

        Optional<Product> result = productUseCase.getProductByUuid(productUuid);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
        verify(productRepositoryPort).findByUuid(productUuid);
    }

    @Test
    @DisplayName("Should return empty when product not found by UUID")
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepositoryPort.findByUuid(productUuid))
                .thenReturn(Optional.empty());

        Optional<Product> result = productUseCase.getProductByUuid(productUuid);

        assertThat(result).isEmpty();
        verify(productRepositoryPort).findByUuid(productUuid);
    }

    @Test
    @DisplayName("Should search products successfully")
    void shouldSearchProductsSuccessfully() {
        ProductSearchCriteria criteria = mock(ProductSearchCriteria.class);
        org.springframework.data.domain.Page<Product> mockPage = mock(org.springframework.data.domain.Page.class);

        when(mockPage.getContent()).thenReturn(List.of(testProduct));
        when(mockPage.getNumber()).thenReturn(0);
        when(mockPage.getSize()).thenReturn(10);
        when(mockPage.getTotalElements()).thenReturn(1L);

        when(productRepositoryPort.searchProducts(criteria))
                .thenReturn(mockPage);

        PageResponse<Product> result = productUseCase.searchProducts(criteria);

        assertThat(result.content()).hasSize(1);
        verify(productRepositoryPort).searchProducts(criteria);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        when(productRepositoryPort.findByUuid(productUuid))
                .thenReturn(Optional.of(testProduct));

        when(productRepositoryPort.updateByUuid(eq(productUuid), any(Product.class)))
                .thenReturn(testProduct);

        Product result = productUseCase.updateProduct(productUuid, testProduct);

        assertThat(result).isNotNull();
        verify(productRepositoryPort).updateByUuid(eq(productUuid), any(Product.class));
    }

    @Test
    @DisplayName("Should soft delete product successfully")
    void shouldDeleteProductSuccessfully() {
        when(productRepositoryPort.findByUuid(productUuid))
                .thenReturn(Optional.of(testProduct));

        productUseCase.deleteProductByUuid(productUuid);

        verify(productRepositoryPort).updateByUuid(
                eq(productUuid),
                argThat(p -> p.getStatus() == ProductStatus.ARCHIVED)
        );
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void shouldThrowWhenDeletingNonExistentProduct() {
        when(productRepositoryPort.findByUuid(productUuid))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productUseCase.deleteProductByUuid(productUuid));

        verify(productRepositoryPort, never()).updateByUuid(any(), any());
    }

    @Test
    @DisplayName("Should auto-generate slug when missing")
    void shouldAutoGenerateSlugWhenMissing() {
        Product productWithoutSlug = new Product(
                null,
                UUID.randomUUID(),
                "My Cool Product",
                "Desc",
                new BigDecimal("10.00"),
                testCategory,
                BigDecimal.ZERO,
                List.of(),
                "SKU-AUTO",
                10,
                ProductStatus.DRAFT,
                null,
                null,
                null
        );

        when(productRepositoryPort.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Product result = productUseCase.createProduct(productWithoutSlug);

        assertThat(result.getSlug()).isEqualTo("my-cool-product");
    }
}
