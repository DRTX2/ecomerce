package com.drtx.ecomerce.amazon.adapters.in.rest.appeal;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.ResolveAppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers.AppealRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import com.drtx.ecomerce.amazon.core.model.issues.AppealStatus;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appeal Controller Tests (Standalone)")
class AppealControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AppealUseCasePort appealUseCasePort;

    @Mock
    private AppealRestMapper appealMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Appeal testAppeal;
    private AppealResponse testAppealResponse;
    private AppealRequest testAppealRequest;
    private ResolveAppealRequest testResolveRequest;

    @BeforeEach
    void setUp() {
        // Mock Security Context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("userPrincipal");
        lenient().when(authentication.getName()).thenReturn("seller@example.com");

        AppealController controller = new AppealController(appealUseCasePort, appealMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        testAppeal = new Appeal();
        testAppeal.setId(1L);
        testAppeal.setStatus(AppealStatus.PENDING);

        testAppealResponse = new AppealResponse(
                1L,
                null, // Incidence response null for simplicity
                null,
                "Unfair ban",
                LocalDateTime.now(),
                AppealStatus.PENDING,
                null,
                AppealDecision.PENDING,
                null);

        testAppealRequest = new AppealRequest(10L, "Unfair ban");
        testResolveRequest = new ResolveAppealRequest(AppealDecision.GRANTED);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /appeals - Should create appeal")
    void testCreateAppeal() throws Exception {
        // Given
        when(appealUseCasePort.createAppeal(eq(10L), eq("Unfair ban"), eq("seller@example.com")))
                .thenReturn(testAppeal);
        when(appealMapper.toResponse(testAppeal)).thenReturn(testAppealResponse);

        // When & Then
        mockMvc.perform(post("/appeals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppealRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(appealUseCasePort, times(1)).createAppeal(eq(10L), eq("Unfair ban"), eq("seller@example.com"));
    }

    @Test
    @DisplayName("POST /appeals - Should return 400 when request invalid (Incidence ID null)")
    void testCreateAppeal_ValidationFail() throws Exception {
        // Given
        AppealRequest invalidRequest = new AppealRequest(null, "Reason");

        // When & Then
        mockMvc.perform(post("/appeals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(appealUseCasePort, never()).createAppeal(anyLong(), anyString(), anyString());
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
                .andExpect(jsonPath("$.id", is(1)));

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
    @DisplayName("PUT /appeals/{id}/resolve - Should resolve appeal")
    void testResolveAppeal() throws Exception {
        // Given
        when(appealUseCasePort.resolveAppeal(eq(1L), eq(AppealDecision.GRANTED), eq("seller@example.com")))
                .thenReturn(testAppeal);
        when(appealMapper.toResponse(testAppeal)).thenReturn(testAppealResponse);

        // When & Then
        mockMvc.perform(put("/appeals/{id}/resolve", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResolveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(appealUseCasePort, times(1)).resolveAppeal(eq(1L), eq(AppealDecision.GRANTED), eq("seller@example.com"));
    }

    @Test
    @DisplayName("PUT /appeals/{id}/resolve - Should return 400 when decision is null")
    void testResolveAppeal_ValidationFail() throws Exception {
        // Given
        ResolveAppealRequest invalidRequest = new ResolveAppealRequest(null);

        // When & Then
        mockMvc.perform(put("/appeals/{id}/resolve", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(appealUseCasePort, never()).resolveAppeal(anyLong(), any(), anyString());
    }
}
