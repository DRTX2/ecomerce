package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.model.UserRole;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;
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
@DisplayName("User Controller Tests")
class UserControllerTest {

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @Mock
        private UserUseCasePort userUseCasePort;

        @Mock
        private UserRestMapper userRestMapper;

        private User testUser;
        private UserRequest testUserRequest;
        private UserResponse testUserResponse;

        @BeforeEach
        void setUp() {
                UserController controller = new UserController(userUseCasePort, userRestMapper);
                mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
                objectMapper = new ObjectMapper();

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

                testUserResponse = new UserResponse(
                                1L,
                                "John Doe",
                                "john@example.com",
                                "USER",
                                "123 Main St",
                                "555-1234");
        }

        @Test
        @DisplayName("GET /users/ - Should return all users")
        void testGetAllUsers() throws Exception {
                // Given
                List<User> users = Arrays.asList(testUser);
                when(userUseCasePort.getAllUsers()).thenReturn(users);
                when(userRestMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(get("/users/")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name", is("John Doe")))
                                .andExpect(jsonPath("$[0].email", is("john@example.com")));

                verify(userUseCasePort, times(1)).getAllUsers();
                verify(userRestMapper, times(1)).toResponse(any(User.class));
        }

        @Test
        @DisplayName("GET /users/{id} - Should return user when found")
        void testGetUserById_Found() throws Exception {
                // Given
                when(userUseCasePort.getUserById(1L)).thenReturn(Optional.of(testUser));
                when(userRestMapper.toResponse(testUser)).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(get("/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userUseCasePort, times(1)).getUserById(1L);
                verify(userRestMapper, times(1)).toResponse(testUser);
        }

        @Test
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
        @DisplayName("PUT /users/{id} - Should update user")
        void testUpdateUser() throws Exception {
                // Given
                when(userRestMapper.toDomain(any(UserRequest.class))).thenReturn(testUser);
                when(userUseCasePort.updateUser(eq(1L), any(User.class))).thenReturn(testUser);
                when(userRestMapper.toResponse(testUser)).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(put("/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userRestMapper, times(1)).toDomain(any(UserRequest.class));
                verify(userUseCasePort, times(1)).updateUser(eq(1L), any(User.class));
                verify(userRestMapper, times(1)).toResponse(testUser);
        }

        @Test
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
