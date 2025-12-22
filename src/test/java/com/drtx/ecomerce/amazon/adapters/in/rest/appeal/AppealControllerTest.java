package com.drtx.ecomerce.amazon.adapters.in.rest.appeal;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.ResolveAppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers.AppealRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.*;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import com.drtx.ecomerce.amazon.infrastructure.security.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppealController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Appeal Controller Integration Tests")
class AppealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppealUseCasePort appealUseCasePort;

    @MockitoBean
    private AppealRestMapper appealMapper;

    private Appeal testAppeal;
    private AppealResponse testAppealResponse;
    private Incidence testIncidence;

    @BeforeEach
    void setUp() {
        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        User seller = new User();
        seller.setId(1L);
        seller.setEmail("seller@example.com");

        testIncidence = new Incidence();
        testIncidence.setId(1L);
        testIncidence.setPublicUi(UUID.randomUUID());
        testIncidence.setProduct(testProduct);
        testIncidence.setStatus(IncidenceStatus.DECIDED);

        testAppeal = new Appeal();
        testAppeal.setId(1L);
        testAppeal.setIncidence(testIncidence);
        testAppeal.setSeller(seller);
        testAppeal.setReason("I believe this decision is incorrect");
        testAppeal.setCreatedAt(LocalDateTime.now());
        testAppeal.setStatus(AppealStatus.PENDING);

        UserResponse sellerResponse = new UserResponse(1L, "Seller", "seller@example.com", "USER", "Address", "Phone");
        IncidenceResponse incidenceResponse = new IncidenceResponse(
                1L, UUID.randomUUID(), null, IncidenceStatus.DECIDED, LocalDateTime.now(), false, null, null, IncidenceDecision.DELETE, null
        );

        testAppealResponse = new AppealResponse(
                1L,
                incidenceResponse,
                sellerResponse,
                "I believe this decision is incorrect",
                LocalDateTime.now(),
                AppealStatus.PENDING,
                null,
                AppealDecision.PENDING,
                null
        );
    }

    @Test
    @WithMockUser(username = "seller@example.com")
    @DisplayName("POST /appeals - Should create appeal")
    void testCreateAppeal() throws Exception {
        // Given
        AppealRequest request = new AppealRequest(1L, "I believe this decision is incorrect");
        when(appealUseCasePort.createAppeal(eq(1L), anyString(), anyString())).thenReturn(testAppeal);
        when(appealMapper.toResponse(testAppeal)).thenReturn(testAppealResponse);

        // When & Then
        mockMvc.perform(post("/appeals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reason", is("I believe this decision is incorrect")));

        verify(appealUseCasePort, times(1)).createAppeal(eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("GET /appeals/{id} - Should return appeal when found")
    void testGetAppeal_Found() throws Exception {
        // Given
        when(appealUseCasePort.getAppealById(1L)).thenReturn(Optional.of(testAppeal));
        when(appealMapper.toResponse(testAppeal)).thenReturn(testAppealResponse);

        // When & Then
        mockMvc.perform(get("/appeals/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reason", is("I believe this decision is incorrect")));

        verify(appealUseCasePort, times(1)).getAppealById(1L);
    }

    @Test
    @DisplayName("GET /appeals/{id} - Should return 404 when appeal not found")
    void testGetAppeal_NotFound() throws Exception {
        // Given
        when(appealUseCasePort.getAppealById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/appeals/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appealUseCasePort, times(1)).getAppealById(999L);
    }

    @Test
    @WithMockUser(username = "moderator@example.com", roles = {"ADMIN"})
    @DisplayName("PUT /appeals/{id}/resolve - Should resolve appeal")
    void testResolveAppeal() throws Exception {
        // Given
        ResolveAppealRequest request = new ResolveAppealRequest(AppealDecision.GRANTED);
        testAppeal.setFinalDecision(AppealDecision.GRANTED);
        testAppeal.setStatus(AppealStatus.RESOLVED);

        when(appealUseCasePort.resolveAppeal(eq(1L), any(), anyString())).thenReturn(testAppeal);
        when(appealMapper.toResponse(testAppeal)).thenReturn(testAppealResponse);

        // When & Then
        mockMvc.perform(put("/appeals/{id}/resolve", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(appealUseCasePort, times(1)).resolveAppeal(eq(1L), any(), anyString());
    }
}
