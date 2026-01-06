package com.drtx.ecomerce.amazon.adapters.in.rest.product;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductUseCasePort productUseCasePort;

    @Mock
    private com.drtx.ecomerce.amazon.application.usecases.product.UploadProductImageUseCase uploadImageUseCase;

    @Mock
    private ProductRestMapper productMapper;

    private Product testProduct;
    private ProductRequest testProductRequest;
    private ProductResponse testProductResponse;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController(productUseCasePort, uploadImageUseCase, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("High-performance laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setCategory(testCategory);
        testProduct.setAverageRating(new BigDecimal("4.5"));
        testProduct.setImages(Arrays.asList("image1.jpg", "image2.jpg"));

        testProductRequest = new ProductRequest(
                "Laptop",
                "High-performance laptop",
                999.99,
                1,
                4.5,
                Arrays.asList("image1.jpg", "image2.jpg"),
                "LAPTOP-001",
                100,
                com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE,
                "laptop-high-performance");

        testProductResponse = new ProductResponse(
                1L,
                "Laptop",
                "High-performance laptop",
                999.99,
                testCategory,
                4.5,
                Arrays.asList("image1.jpg", "image2.jpg"),
                "LAPTOP-001",
                100,
                com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE,
                "laptop-high-performance",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /products - Should return all products")
    void testGetAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productUseCasePort.getAllProducts()).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(999.99)));

        verify(productUseCasePort, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("POST /products - Should create new product")
    void testCreateProduct() throws Exception {
        // Given
        when(productMapper.toDomain(any(ProductRequest.class))).thenReturn(testProduct);
        when(productUseCasePort.createProduct(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("GET /products/{id} - Should return product when found")
    void testGetProductById_Found() throws Exception {
        // Given
        when(productUseCasePort.getProductById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /products/{id} - Should return 404 when product not found")
    void testGetProductById_NotFound() throws Exception {
        // Given
        when(productUseCasePort.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productUseCasePort, times(1)).getProductById(999L);
    }

    @Test
    @DisplayName("PUT /products/{id} - Should update product")
    void testUpdateProduct() throws Exception {
        // Given
        when(productMapper.toDomain(any(ProductRequest.class))).thenReturn(testProduct);
        when(productUseCasePort.updateProduct(eq(1L), any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /products/{id} - Should delete product")
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productUseCasePort).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productUseCasePort, times(1)).deleteProduct(1L);
    }
}
