package com.drtx.ecomerce.amazon.adapters.in.rest.favorite;

import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.dto.FavoriteResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.mappers.FavoriteRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.Category;
import com.drtx.ecomerce.amazon.core.model.Favorite;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.FavoriteUseCasePort;
import com.drtx.ecomerce.amazon.infrastructure.security.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Favorite Controller Integration Tests")
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteUseCasePort favoriteUseCasePort;

    @MockitoBean
    private FavoriteRestMapper favoriteMapper;

    @MockitoBean
    private ProductRestMapper productMapper;

    private Favorite testFavorite;
    private FavoriteResponse testFavoriteResponse;
    private Product testProduct;
    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");

        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setCategory(testCategory);

        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setProduct(testProduct);

        UserResponse userResponse = new UserResponse(1L, "Test User", "user@example.com", "USER", "Address", "Phone");
        testFavoriteResponse = new FavoriteResponse(1L, userResponse, testProductResponse, LocalDateTime.now());
        testProductResponse = new ProductResponse(1L, "Laptop", "Description", 999.99, 10, testCategory, 4.5,
                Arrays.asList());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /favorites/product/{productId} - Should add favorite")
    void testAddFavorite() throws Exception {
        // Given
        when(favoriteUseCasePort.addFavorite(eq(1L), anyString())).thenReturn(testFavorite);
        when(favoriteMapper.toResponse(testFavorite)).thenReturn(testFavoriteResponse);

        // When & Then
        mockMvc.perform(post("/favorites/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(favoriteUseCasePort, times(1)).addFavorite(eq(1L), anyString());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("DELETE /favorites/product/{productId} - Should remove favorite")
    void testRemoveFavorite() throws Exception {
        // Given
        doNothing().when(favoriteUseCasePort).removeFavorite(eq(1L), anyString());

        // When & Then
        mockMvc.perform(delete("/favorites/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(favoriteUseCasePort, times(1)).removeFavorite(eq(1L), anyString());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("GET /favorites - Should return user favorites")
    void testGetUserFavorites() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(favoriteUseCasePort.getUserFavorites(anyString())).thenReturn(products);
        when(productMapper.toResponse(any())).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get("/favorites")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")));

        verify(favoriteUseCasePort, times(1)).getUserFavorites(anyString());
    }
}
