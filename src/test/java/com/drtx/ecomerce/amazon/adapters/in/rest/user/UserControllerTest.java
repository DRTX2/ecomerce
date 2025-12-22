package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.model.UserRole;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Controller Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserUseCasePort userUseCasePort;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(UserRole.USER);
        testUser.setAddress("123 Main St");
        testUser.setPhone("555-1234");

        testUserRequest = new UserRequest(
                "John Doe",
                "john@example.com",
                UserRole.USER,
                "123 Main St",
                "555-1234",
                "password123");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /users/ - Should return all users")
    void testGetAllUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userUseCasePort.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));

        verify(userUseCasePort, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /users/{id} - Should return user when found")
    void testGetUserById_Found() throws Exception {
        // Given
        when(userUseCasePort.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userUseCasePort, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /users/{id} - Should return 404 when user not found")
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userUseCasePort.getUserById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userUseCasePort, times(1)).getUserById(999L);
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /users/{id} - Should update user")
    void testUpdateUser() throws Exception {
        // Given
        when(userUseCasePort.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(put("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userUseCasePort, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /users/{id} - Should delete user")
    void testDeleteUser() throws Exception {
        // Given
        doNothing().when(userUseCasePort).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userUseCasePort, times(1)).deleteUser(1L);
    }
}
