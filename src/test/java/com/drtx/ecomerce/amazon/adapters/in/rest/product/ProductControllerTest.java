package com.drtx.ecomerce.amazon.adapters.in.rest.product;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.application.usecases.product.UploadProductImageUseCase;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private UploadProductImageUseCase uploadImageUseCase;

    @Mock
    private ProductRestMapper productMapper;

    private Product testProduct;
    private ProductRequest testProductRequest;
    private ProductResponse testProductResponse;
    private Category testCategory;

    private UUID productUuid;

    private static final String BASE_URL = "/products";

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);



    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController(productUseCasePort, uploadImageUseCase, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices");

        testProduct = new Product();
        testProduct.setId(1L);
        productUuid = UUID.randomUUID();
        testProduct.setUuid(productUuid);
        testProduct.setName("Laptop");
        testProduct.setDescription("High-performance laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setCategory(testCategory);
        testProduct.setAverageRating(new BigDecimal("4.5"));
        testProduct.setImages(List.of("image1.jpg", "image2.jpg"));

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
                ProductStatus.ACTIVE,
                "laptop-high-performance",
                NOW,
                NOW);
    }

    @Test
    @DisplayName("GET /products - Should return all products")
    void testGetAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productUseCasePort.getAllProducts()).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid", is(productUuid.toString())))
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
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(productUuid.toString())))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("POST /products - Should return 400 for invalid request")
    void testCreateProduct_InvalidRequest() throws Exception {
        ProductRequest invalidRequest = new ProductRequest(
                null, null, 0D, 0, 0D,
                List.of(), null, 0, null, null
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /products/{uuid} - Should return product when found")
    void testGetProductById_Found() throws Exception {
        // Given
        when(productUseCasePort.getProductByUuid(productUuid)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL+"/{uuid}", productUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(productUuid.toString())))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).getProductByUuid(productUuid);
    }

    @Test
    @DisplayName("GET /products/{uuid} - Should return 404 when product not found")
    void testGetProductById_NotFound() throws Exception {
        // Given
        when(productUseCasePort.getProductByUuid(productUuid)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get(BASE_URL+"/{uuid}", productUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productUseCasePort, times(1)).getProductByUuid(productUuid);
    }

    @Test
    @DisplayName("PUT /products/{uuid} - Should update product")
    void testUpdateProduct() throws Exception {
        // Given
        when(productMapper.toDomain(any(ProductRequest.class))).thenReturn(testProduct);
        when(productUseCasePort.updateProduct(eq(productUuid), any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(put(BASE_URL+"/{uuid}", productUuid.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(productUuid.toString())))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productUseCasePort, times(1)).updateProduct(eq(productUuid), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /products/{uuid} - Should delete product")
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productUseCasePort).deleteProductByUuid(productUuid);

        // When & Then
        mockMvc.perform(delete(BASE_URL+"/{uuid}", productUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productUseCasePort, times(1)).deleteProductByUuid(productUuid);
    }
}
