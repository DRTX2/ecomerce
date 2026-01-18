package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
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
import java.util.UUID;

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

        private UUID userUuid;

        @BeforeEach
        void setUp() {
                UserController controller = new UserController(userUseCasePort, userRestMapper);
                mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
                objectMapper = new ObjectMapper();

                testUser = new User();

                testUser.setId(1L);
                userUuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
                testUser.setUuid(userUuid);
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
                                userUuid,
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
        @DisplayName("GET /users/{uuid} - Should return user when found")
        void testGetUserByUuid_Found() throws Exception {
                // Given
                when(userUseCasePort.getUserByUuid(userUuid)).thenReturn(Optional.of(testUser));
                when(userRestMapper.toResponse(testUser)).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(get("/users/{uuid}", userUuid.toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userUseCasePort, times(1)).getUserByUuid(userUuid);
                verify(userRestMapper, times(1)).toResponse(testUser);
        }

        @Test
        @DisplayName("GET /users/{uuid} - Should return 404 when user not found")
        void testGetUserByUuid_NotFound() throws Exception {
                // Given
                UUID notFoundUuid = UUID.randomUUID();
                when(userUseCasePort.getUserByUuid(notFoundUuid)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/users/{uuid}", notFoundUuid.toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(userUseCasePort, times(1)).getUserByUuid(notFoundUuid);
        }

        @Test
        @DisplayName("PUT /users/{uuid} - Should update user")
        void testUpdateUser() throws Exception {
                // Given
                when(userRestMapper.toDomain(any(UserRequest.class))).thenReturn(testUser);
                when(userUseCasePort.updateUserByUuid(eq(userUuid), any(User.class))).thenReturn(testUser);
                when(userRestMapper.toResponse(testUser)).thenReturn(testUserResponse);

                // When & Then
                mockMvc.perform(put("/users/{uuid}", userUuid.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userRestMapper, times(1)).toDomain(any(UserRequest.class));
                verify(userUseCasePort, times(1)).updateUserByUuid(eq(userUuid), any(User.class));
                verify(userRestMapper, times(1)).toResponse(testUser);
        }

        @Test
        @DisplayName("DELETE /users/{uuid} - Should delete user")
        void testDeleteUser() throws Exception {
                // Given
                doNothing().when(userUseCasePort).deleteUserByUuid(userUuid);

                // When & Then
                mockMvc.perform(delete("/users/{uuid}", userUuid.toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(userUseCasePort, times(1)).deleteUserByUuid(userUuid);
        }
}
