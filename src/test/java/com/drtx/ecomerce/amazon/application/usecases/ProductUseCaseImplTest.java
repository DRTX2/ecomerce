package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.Category;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
                List.of()
        );

        testProduct = new Product(
                1L,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                50,
                testCategory,
                new BigDecimal("4.5"),
                List.of("image1.jpg", "image2.jpg")
        );
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
                100,
                testCategory,
                new BigDecimal("0.0"),
                List.of("phone1.jpg")
        );

        Product savedProduct = new Product(
                2L,
                "Smartphone",
                "Latest smartphone model",
                new BigDecimal("699.99"),
                100,
                testCategory,
                new BigDecimal("0.0"),
                List.of("phone1.jpg")
        );

        when(productRepositoryPort.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productUseCase.createProduct(newProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Smartphone");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("699.99"));
        assertThat(result.getStock()).isEqualTo(100);
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
        assertThat(result.get().getStock()).isEqualTo(50);
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
                75,
                testCategory,
                new BigDecimal("4.2"),
                List.of("tablet1.jpg")
        );

        Product product3 = new Product(
                3L,
                "Headphones",
                "Wireless headphones",
                new BigDecimal("149.99"),
                200,
                testCategory,
                new BigDecimal("4.7"),
                List.of("headphones1.jpg")
        );

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
                30,
                testCategory,
                new BigDecimal("4.8"),
                List.of("laptop_pro1.jpg", "laptop_pro2.jpg")
        );

        when(productRepositoryPort.updateById(eq(productId), any(Product.class)))
                .thenReturn(updatedProduct);

        // When
        Product result = productUseCase.updateProduct(productId, updatedProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1199.99"));
        assertThat(result.getStock()).isEqualTo(30);
        assertThat(result.getAverageRating()).isEqualByComparingTo(new BigDecimal("4.8"));
        verify(productRepositoryPort, times(1)).updateById(productId, updatedProduct);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Given
        Long productId = 1L;
        doNothing().when(productRepositoryPort).delete(productId);

        // When
        productUseCase.deleteProduct(productId);

        // Then
        verify(productRepositoryPort, times(1)).delete(productId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent product")
    void shouldHandleDeleteForNonExistentProduct() {
        // Given
        Long productId = 999L;
        doNothing().when(productRepositoryPort).delete(productId);

        // When
        productUseCase.deleteProduct(productId);

        // Then
        verify(productRepositoryPort, times(1)).delete(productId);
    }

    @Test
    @DisplayName("Should handle product with zero stock")
    void shouldHandleProductWithZeroStock() {
        // Given
        Product outOfStockProduct = new Product(
                4L,
                "Out of Stock Item",
                "This item is out of stock",
                new BigDecimal("99.99"),
                0,
                testCategory,
                new BigDecimal("3.5"),
                List.of("outofstock.jpg")
        );

        when(productRepositoryPort.save(any(Product.class))).thenReturn(outOfStockProduct);

        // When
        Product result = productUseCase.createProduct(outOfStockProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(0);
        verify(productRepositoryPort, times(1)).save(outOfStockProduct);
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
                25,
                testCategory,
                new BigDecimal("0.0"),
                List.of("newproduct.jpg")
        );

        when(productRepositoryPort.save(any(Product.class))).thenReturn(noRatingProduct);

        // When
        Product result = productUseCase.createProduct(noRatingProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAverageRating()).isEqualByComparingTo(new BigDecimal("0.0"));
        verify(productRepositoryPort, times(1)).save(noRatingProduct);
    }
}
