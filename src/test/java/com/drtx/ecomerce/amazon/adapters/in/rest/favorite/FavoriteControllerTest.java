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
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Favorite Controller Tests (Standalone)")
class FavoriteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FavoriteUseCasePort favoriteUseCasePort;

    @Mock
    private FavoriteRestMapper favoriteMapper;

    @Mock
    private ProductRestMapper productMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Favorite testFavorite;
    private FavoriteResponse testFavoriteResponse;
    private Product testProduct;
    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        // Mock Security Context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("userPrincipal");
        lenient().when(authentication.getName()).thenReturn("user@example.com");

        FavoriteController controller = new FavoriteController(favoriteUseCasePort, favoriteMapper, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

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

        // Constructor ProductResponse: Long id, String name, String description,
        // BigDecimal price, Integer stock, Category category, Double rating, List image
        testProductResponse = new ProductResponse(1L, "Laptop", "Description", 999.99, 10, testCategory, 4.5,
                Collections.emptyList());

        testFavoriteResponse = new FavoriteResponse(1L, userResponse, testProductResponse, LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /favorites/product/{productId} - Should add favorite for authenticated user")
    void testAddFavorite() throws Exception {
        // Given
        when(favoriteUseCasePort.addFavorite(eq(1L), eq("user@example.com"))).thenReturn(testFavorite);
        when(favoriteMapper.toResponse(testFavorite)).thenReturn(testFavoriteResponse);

        // When & Then
        mockMvc.perform(post("/favorites/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(favoriteUseCasePort, times(1)).addFavorite(eq(1L), eq("user@example.com"));
    }

    @Test
    @DisplayName("DELETE /favorites/product/{productId} - Should remove favorite for authenticated user")
    void testRemoveFavorite() throws Exception {
        // Given
        doNothing().when(favoriteUseCasePort).removeFavorite(eq(1L), eq("user@example.com"));

        // When & Then
        mockMvc.perform(delete("/favorites/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(favoriteUseCasePort, times(1)).removeFavorite(eq(1L), eq("user@example.com"));
    }

    @Test
    @DisplayName("GET /favorites - Should return user favorites")
    void testGetUserFavorites() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(favoriteUseCasePort.getUserFavorites(eq("user@example.com"))).thenReturn(products);
        when(productMapper.toResponse(any(Product.class))).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get("/favorites")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")));

        verify(favoriteUseCasePort, times(1)).getUserFavorites(eq("user@example.com"));
    }
}
