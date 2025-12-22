package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper;
import com.drtx.ecomerce.amazon.application.usecases.auth.AuthService;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.model.UserRole;
import com.drtx.ecomerce.amazon.infrastructure.security.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Auth Controller Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserSecurityMapper userSecurityMapper;

    private User testUser;
    private AuthResponse testAuthResponse;
    private RegisterRequest testRegisterRequest;
    private AuthRequest testAuthRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(UserRole.USER);
        testUser.setAddress("123 Main St");
        testUser.setPhone("555-1234");

        testAuthResponse = new AuthResponse("mock-jwt-token");

        testRegisterRequest = new RegisterRequest(
                "John Doe",
                "john@example.com",
                UserRole.USER,
                "123 Main St",
                "555-1234",
                "password123");

        testAuthRequest = new AuthRequest("john@example.com", "password123");
    }

    @Test
    @DisplayName("POST /auth/register - Should register new user")
    void testRegister() throws Exception {
        // Given
        when(userSecurityMapper.registerRequestToDomain(any(RegisterRequest.class))).thenReturn(testUser);
        when(authService.register(any(User.class))).thenReturn(testUser);
        when(userSecurityMapper.entityToResponse(testUser)).thenReturn(testAuthResponse);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")));

        verify(authService, times(1)).register(any(User.class));
    }

    @Test
    @DisplayName("POST /auth/login - Should login user and return token")
    void testLogin() throws Exception {
        // Given
        when(authService.login(any(AuthRequest.class))).thenReturn(testAuthResponse);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")));

        verify(authService, times(1)).login(any(AuthRequest.class));
    }

    @Test
    @DisplayName("POST /auth/logout - Should logout user")
    void testLogout() throws Exception {
        // Given
        String authHeader = "Bearer mock-jwt-token";
        doNothing().when(authService).logout(anyString());

        // When & Then
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authService, times(1)).logout(authHeader);
    }
}
