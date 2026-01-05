package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper;
import com.drtx.ecomerce.amazon.core.model.security.AuthResult;
import com.drtx.ecomerce.amazon.core.model.security.LoginCommand;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.in.rest.security.AuthUseCasePort;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Tests (Standalone)")
class AuthControllerTest {

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @Mock
        private AuthUseCasePort authService;

        @Mock
        private UserSecurityMapper userSecurityMapper;

        private User testUser;
        private AuthResult testAuthResult;
        private RegisterRequest testRegisterRequest;
        private AuthRequest testAuthRequest;

        @BeforeEach
        void setUp() {
                AuthController authController = new AuthController(authService, userSecurityMapper);
                mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
                objectMapper = new ObjectMapper();

                testUser = new User();
                testUser.setId(1L);
                testUser.setName("John Doe");
                testUser.setEmail("john@example.com");
                testUser.setRole(UserRole.USER);
                testUser.setAddress("123 Main St");
                testUser.setPhone("555-1234");

                testAuthResult = new AuthResult(testUser, "mock-jwt-token", "", 86400000L);

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
                when(authService.register(any(User.class))).thenReturn(testAuthResult);

                // When & Then
                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRegisterRequest)))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("mock-jwt-token")));

                verify(authService, times(1)).register(any(User.class));
        }

        @Test
        @DisplayName("POST /auth/register - Should return 400 when request invalid (Invalid Email)")
        void testRegister_InvalidEmail() throws Exception {
                // Given
                RegisterRequest invalidRequest = new RegisterRequest(
                                "John Doe",
                                "not-an-email", // Email inválido
                                UserRole.USER,
                                "123 Main St",
                                "555-1234",
                                "password123");

                // When & Then
                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).register(any(User.class));
        }

        @Test
        @DisplayName("POST /auth/login - Should login user and return token")
        void testLogin() throws Exception {
                // Given
                when(authService.login(any(LoginCommand.class))).thenReturn(testAuthResult);

                // When & Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testAuthRequest)))
                                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("mock-jwt-token")));

                verify(authService, times(1)).login(any(LoginCommand.class));
        }

        @Test
        @DisplayName("POST /auth/login - Should return 400 when request invalid (Short Password)")
        void testLogin_InvalidPassword() throws Exception {
                // Given
                AuthRequest invalidRequest = new AuthRequest("john@example.com", "short"); // Password muy corto (<8)

                // When & Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(authService, never()).login(any(LoginCommand.class));
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

                verify(authService, times(1)).logout("mock-jwt-token");
        }
}
