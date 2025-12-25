package com.drtx.ecomerce.amazon.adapters.in.rest.category;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers.CategoryRestMapper;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
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
@DisplayName("Category Controller Tests (Standalone)")
class CategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CategoryUseCasePort categoryUseCasePort;

    @Mock
    private CategoryRestMapper categoryMapper;

    private Category testCategory;
    private CategoryRequest testCategoryRequest;
    private CategoryResponse testCategoryResponse;

    @BeforeEach
    void setUp() {
        // Inicializamos el controlador con los mocks y configuramos MockMvc en modo
        // standalone
        CategoryController controller = new CategoryController(categoryUseCasePort, categoryMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices and accessories");

        testCategoryRequest = new CategoryRequest("Electronics", "Electronic devices and accessories");
        testCategoryResponse = new CategoryResponse(1L, "Electronics", "Electronic devices and accessories");
    }

    @Test
    @DisplayName("GET /categories/ - Should return all categories")
    void testGetAllCategories() throws Exception {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryUseCasePort.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(get("/categories/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Electronics")));

        verify(categoryUseCasePort, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("POST /categories/ - Should create new category")
    void testCreateCategory() throws Exception {
        // Given
        when(categoryMapper.toDomain(any(CategoryRequest.class))).thenReturn(testCategory);
        when(categoryUseCasePort.createCategory(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(post("/categories/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")));

        verify(categoryUseCasePort, times(1)).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("POST /categories/ - Should return 400 Bad Request when name is empty")
    void testCreateCategory_ValidationFail() throws Exception {
        // Given
        CategoryRequest invalidRequest = new CategoryRequest("", "Description");

        // When & Then
        mockMvc.perform(post("/categories/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Esperamos 400 por @NotBlank

        verify(categoryUseCasePort, never()).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return category when found")
    void testGetCategoryById_Found() throws Exception {
        // Given
        when(categoryUseCasePort.getCategoryById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(get("/categories/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")));

        verify(categoryUseCasePort, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 404 when category not found")
    void testGetCategoryById_NotFound() throws Exception {
        // Given
        when(categoryUseCasePort.getCategoryById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/categories/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryUseCasePort, times(1)).getCategoryById(999L);
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should update category")
    void testUpdateCategory() throws Exception {
        // Given
        when(categoryMapper.toDomain(any(CategoryRequest.class))).thenReturn(testCategory);
        when(categoryUseCasePort.updateCategory(eq(1L), any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // When & Then
        mockMvc.perform(put("/categories/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")));

        verify(categoryUseCasePort, times(1)).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should delete category")
    void testDeleteCategory() throws Exception {
        // Given
        doNothing().when(categoryUseCasePort).deleteCategory(1L);

        // When & Then
        mockMvc.perform(delete("/categories/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryUseCasePort, times(1)).deleteCategory(1L);
    }
}
