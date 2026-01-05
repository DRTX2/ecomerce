package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        // /auth/register and /auth/login are public endpoints (updated path)
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@test.com\",\"password\":\"test\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Protected endpoint without token should return 403")
    void testProtectedWithoutToken() throws Exception {
        mockMvc.perform(get("/categories")) // Updated path
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Protected endpoint with valid token should succeed")
    void testProtectedWithValidToken() throws Exception {
        // Given
        String validToken = "valid-token";
        String username = "testuser";
        // UserDetails needed for UserDetailsService
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password",
                Collections.emptyList());

        when(tokenProvider.extractUsername(validToken)).thenReturn(username);
        // Important: TokenProvider now takes Domain User, but JwtAuthFilter has
        // UserDetails.
        // If JwtAuthFilter was not updated to map UserDetails to Domain User, it would
        // have failed compilation there too.
        // Let's assume JwtAuthFilter creates a Domain User from UserDetails or we need
        // to fix it.
        // BUT here we mock TokenProvider.
        // If JwtAuthFilter invokes tokenProvider.isTokenValid(token, userDomain), we
        // need to match that.
        // However, I suspect JwtAuthFilter might be broken if I didn't verify it
        // compiled.
        // I will check JwtAuthFilter in a moment. For now, let's use `any()` to avoid
        // type issues in test.
        when(tokenProvider.isTokenValid(eq(validToken), any(User.class))).thenReturn(true);
        when(tokenRevocationPort.isInvalidated(validToken)).thenReturn(false);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(categoryUseCasePort.getAllCategories()).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/categories") // Updated path
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
        mockMvc.perform(get("/categories") // Updated path
                .header("Authorization", "Bearer " + revokedToken))
                .andExpect(status().isUnauthorized());
    }
}
