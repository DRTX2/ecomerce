package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security Integration Tests")
@TestPropertySource(properties = {
        "security.jwt.secret-key=${TEST_JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}",
        "security.jwt.expiration=86400000",
        "spring.main.allow-bean-definition-overriding=true"
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private TokenRevocationPort tokenRevocationPort;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CategoryUseCasePort categoryUseCasePort;

    @Test
    @DisplayName("Public endpoints should be accessible without token")
    void testPublicEndpoint() throws Exception {
        // /api/auth/register and /api/auth/login are public endpoints
        // POST to /api/auth/login should not return 403 (forbidden)
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().is4xxClientError()); // 400 or 401, but not 403 (which would mean access denied)
    }

    @Test
    @DisplayName("Protected endpoint without token should return 403")
    void testProtectedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Protected endpoint with valid token should succeed")
    void testProtectedWithValidToken() throws Exception {
        // Given
        String validToken = "valid-token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(tokenProvider.extractUsername(validToken)).thenReturn(username);
        when(tokenProvider.isTokenValid(validToken, userDetails)).thenReturn(true);
        when(tokenRevocationPort.isInvalidated(validToken)).thenReturn(false);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(categoryUseCasePort.getAllCategories()).thenReturn(Collections.emptyList());

        // When/Then
        // With valid authentication, the endpoint should return 200 OK
        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected endpoint with revoked token should return 401")
    void testProtectedWithRevokedToken() throws Exception {
        // Given
        String revokedToken = "revoked-token";
        when(tokenProvider.extractUsername(revokedToken)).thenReturn("user");
        when(tokenRevocationPort.isInvalidated(revokedToken)).thenReturn(true);

        // When/Then
        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer " + revokedToken))
                .andExpect(status().isUnauthorized());
    }
}
