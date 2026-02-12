package com.drtx.ecomerce.amazon.adapters.in.rest.incidence;

import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ReportRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ResolveIncidenceRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Incidence Controller Tests (Standalone)")
class IncidenceControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IncidenceUseCasePort incidenceUseCasePort;

    @Mock
    private IncidenceRestMapper incidenceMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Incidence testIncidence;
    private IncidenceResponse testIncidenceResponse;
    private ReportRequest testReportRequest;
    private ResolveIncidenceRequest testResolveRequest;

    @BeforeEach
    void setUp() {
        // Mock Security Context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn("userPrincipal");
        lenient().when(authentication.getName()).thenReturn("user@example.com");

        IncidenceController controller = new IncidenceController(incidenceUseCasePort, incidenceMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        testIncidence = new Incidence();
        testIncidence.setId(1L);
        testIncidence.setUuid(UUID.randomUUID());
        testIncidence.setStatus(IncidenceStatus.OPEN);

        testIncidenceResponse = new IncidenceResponse(
                testIncidence.getUuid(),
                null,
                IncidenceStatus.OPEN,
                LocalDateTime.now(),
                false,
                null,
                "Comment",
                IncidenceDecision.DELETE,
                Collections.emptyList());

        testReportRequest = new ReportRequest("Spam", "This is spam");
        testResolveRequest = new ResolveIncidenceRequest(IncidenceDecision.DELETE, "Banned for spam");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /incidences/product/{productUuid} - Should create incidence")
    void testReportProduct() throws Exception {
        // Given
        UUID productUuid = UUID.randomUUID();
        when(incidenceMapper.toDomain(any(ReportRequest.class))).thenReturn(new Report());
        when(incidenceUseCasePort.createIncidence(eq(productUuid), any(Report.class), eq("user@example.com")))
                .thenReturn(testIncidence);
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(post("/incidences/product/{productUuid}", productUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReportRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OPEN")));

        verify(incidenceUseCasePort, times(1)).createIncidence(eq(productUuid), any(Report.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("POST /incidences/product/{productId} - Should return 400 when reason is empty")
    void testReportProduct_ValidationFail() throws Exception {
        // Given
        ReportRequest invalidRequest = new ReportRequest("", "Comment"); // Reason blank

        // When & Then
        mockMvc.perform(post("/incidences/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(incidenceUseCasePort, never()).createIncidence(anyLong(), any(Report.class), anyString());
    }

    @Test
    @DisplayName("GET /incidences - Should return all incidences")
    void testGetAllIncidences() throws Exception {
        // Given
        List<Incidence> incidences = Arrays.asList(testIncidence);
        when(incidenceUseCasePort.getAllIncidences()).thenReturn(incidences);
        when(incidenceMapper.toResponse(any(Incidence.class))).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(get("/incidences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid", is(testIncidence.getUuid().toString())));

        verify(incidenceUseCasePort, times(1)).getAllIncidences();
    }

    @Test
    @DisplayName("GET /incidences/{uuid} - Should return incidence when found")
    void testGetIncidenceByUuid_Found() throws Exception {
        // Given
        UUID uuid = testIncidence.getUuid();
        when(incidenceUseCasePort.getIncidenceByUuid(uuid)).thenReturn(Optional.of(testIncidence));
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(get("/incidences/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.uuid", is(uuid.toString())));

        verify(incidenceUseCasePort, times(1)).getIncidenceByUuid(uuid);
    }

    @Test
    @DisplayName("GET /incidences/{id} - Should return 404 when incidence not found")
    void testGetIncidenceById_NotFound() throws Exception {
        // Given
        when(incidenceUseCasePort.getIncidenceById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/incidences/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(incidenceUseCasePort, times(1)).getIncidenceById(999L);
    }

    @Test
    @DisplayName("PUT /incidences/{uuid}/resolve - Should resolve incidence (MODERATOR only)")
    void testResolveIncidenceByUuid() throws Exception {
        // Given
        UUID uuid = testIncidence.getUuid();
        when(incidenceUseCasePort.resolveIncidenceByUuid(eq(uuid), eq(IncidenceDecision.DELETE), anyString(), eq("user@example.com")))
                .thenReturn(testIncidence);
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(put("/incidences/{uuid}/resolve", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testResolveRequest)))
                .andExpect(status().isOk());

        verify(incidenceUseCasePort, times(1)).resolveIncidenceByUuid(eq(uuid), eq(IncidenceDecision.DELETE), anyString(), eq("user@example.com"));
    }
}
