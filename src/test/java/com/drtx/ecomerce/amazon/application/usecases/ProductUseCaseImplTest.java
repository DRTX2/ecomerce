package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.application.usecases.product.ProductUseCaseImpl;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUseCaseImpl Unit Tests")
class ProductUseCaseImplTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category(
                1L,
                "Electronics",
                "Electronic devices",
                List.of());

        testProduct = new Product(
                1L,
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
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        Product newProduct = new Product(
                null,
                "Smartphone",
                "Latest smartphone model",
                new BigDecimal("699.99"),
                testCategory,
                new BigDecimal("0.0"),
                List.of("phone1.jpg"),
                "PHONE-001",
                100,
                ProductStatus.DRAFT,
                "smartphone",
                null,
                null);

        Product savedProduct = new Product(
                2L,
                "Smartphone",
                "Latest smartphone model",
                new BigDecimal("699.99"),
                testCategory,
                new BigDecimal("0.0"),
                List.of("phone1.jpg"),
                "PHONE-001",
                100,
                ProductStatus.DRAFT,
                "smartphone",
                LocalDateTime.now(),
                LocalDateTime.now());

        when(productRepositoryPort.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productUseCase.createProduct(newProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Smartphone");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("699.99"));
        verify(productRepositoryPort, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productUseCase.getProductById(productId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(productId);
        assertThat(result.get().getName()).isEqualTo("Laptop");
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(result.get().getAverageRating()).isEqualByComparingTo(new BigDecimal("4.5"));
        verify(productRepositoryPort, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void shouldReturnEmptyWhenProductNotFoundById() {
        // Given
        Long productId = 999L;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productUseCase.getProductById(productId);

        // Then
        assertThat(result).isEmpty();
        verify(productRepositoryPort, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() {
        // Given
        Product product2 = new Product(
                2L,
                "Tablet",
                "Portable tablet device",
                new BigDecimal("499.99"),
                testCategory,
                new BigDecimal("4.2"),
                List.of("tablet1.jpg"),
                "TABLET-001",
                30,
                ProductStatus.ACTIVE,
                "tablet",
                LocalDateTime.now(),
                LocalDateTime.now());

        Product product3 = new Product(
                3L,
                "Headphones",
                "Wireless headphones",
                new BigDecimal("149.99"),
                testCategory,
                new BigDecimal("4.7"),
                List.of("headphones1.jpg"),
                "HEADPHONES-001",
                200,
                ProductStatus.ACTIVE,
                "headphones",
                LocalDateTime.now(),
                LocalDateTime.now());

        List<Product> products = Arrays.asList(testProduct, product2, product3);
        when(productRepositoryPort.findAll()).thenReturn(products);

        // When
        List<Product> result = productUseCase.getAllProducts();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(testProduct, product2, product3);
        verify(productRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExist() {
        // Given
        when(productRepositoryPort.findAll()).thenReturn(List.of());

        // When
        List<Product> result = productUseCase.getAllProducts();

        // Then
        assertThat(result).isEmpty();
        verify(productRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        Long productId = 1L;
        Product updatedProduct = new Product(
                productId,
                "Laptop Pro",
                "Updated high-performance laptop",
                new BigDecimal("1199.99"),
                testCategory,
                new BigDecimal("4.8"),
                List.of("laptop_pro1.jpg", "laptop_pro2.jpg"),
                "LAPTOP-PRO-001",
                20,
                ProductStatus.ACTIVE,
                "laptop-pro",
                LocalDateTime.now(),
                LocalDateTime.now());

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepositoryPort.updateById(eq(productId), any(Product.class)))
                .thenReturn(updatedProduct);

        // When
        Product result = productUseCase.updateProduct(productId, updatedProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1199.99"));
        assertThat(result.getAverageRating()).isEqualByComparingTo(new BigDecimal("4.8"));
        verify(productRepositoryPort, times(1)).updateById(productId, updatedProduct);
    }

    @Test
    @DisplayName("Should delete product successfully (Soft Delete)")
    void shouldDeleteProductSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepositoryPort.updateById(eq(productId), any(Product.class))).thenReturn(testProduct);

        // When
        productUseCase.deleteProduct(productId);

        // Then
        verify(productRepositoryPort, times(1)).findById(productId);
        verify(productRepositoryPort, times(1)).updateById(eq(productId),
                argThat(p -> p.getStatus() == ProductStatus.ARCHIVED));
        verify(productRepositoryPort, never()).delete(productId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void shouldHandleDeleteForNonExistentProduct() {
        // Given
        Long productId = 999L;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.empty());

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            productUseCase.deleteProduct(productId);
        });

        verify(productRepositoryPort, times(1)).findById(productId);
        verify(productRepositoryPort, never()).delete(productId);
        verify(productRepositoryPort, never()).updateById(any(), any());
    }

    @Test
    @DisplayName("Should auto-generate slug when creating product with missing slug")
    void shouldAutoGenerateSlugWhenMissingInCreate() {
        // Given
        Product productWithoutSlug = new Product(
                null, "My Cool Product", "Desc", new BigDecimal("10.00"),
                testCategory, BigDecimal.ZERO, List.of(), "SKU-AUTO", 10,
                ProductStatus.DRAFT, null, null, null); // Slug is null

        when(productRepositoryPort.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            // Simulate DB save returning ID
            return new Product(100L, p.getName(), p.getDescription(), p.getPrice(),
                    p.getCategory(), p.getAverageRating(), p.getImages(),
                    p.getSku(), p.getStockQuantity(), p.getStatus(),
                    p.getSlug(), LocalDateTime.now(), LocalDateTime.now());
        });

        // When
        Product result = productUseCase.createProduct(productWithoutSlug);

        // Then
        assertThat(result.getSlug()).isEqualTo("my-cool-product");
        verify(productRepositoryPort).save(argThat(p -> p.getSlug().equals("my-cool-product")));
    }

    @Test
    @DisplayName("Should handle product with no rating")
    void shouldHandleProductWithNoRating() {
        // Given
        Product noRatingProduct = new Product(
                5L,
                "New Product",
                "Brand new product with no reviews",
                new BigDecimal("199.99"),
                testCategory,
                new BigDecimal("0.0"),
                List.of("newproduct.jpg"),
                "NEW-001",
                10,
                ProductStatus.DRAFT,
                "new-product",
                LocalDateTime.now(),
                LocalDateTime.now());

        when(productRepositoryPort.save(any(Product.class))).thenReturn(noRatingProduct);

        // When
        Product result = productUseCase.createProduct(noRatingProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAverageRating()).isEqualByComparingTo(new BigDecimal("0.0"));
        verify(productRepositoryPort, times(1)).save(noRatingProduct);
    }
}
