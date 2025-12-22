package com.drtx.ecomerce.amazon.adapters.in.rest.incidence;

import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ReportRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ResolveIncidenceRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.core.model.*;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidenceController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Incidence Controller Integration Tests")
class IncidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IncidenceUseCasePort incidenceUseCasePort;

    @MockitoBean
    private IncidenceRestMapper incidenceMapper;

    private Incidence testIncidence;
    private IncidenceResponse testIncidenceResponse;
    private Report testReport;
    private ReportRequest testReportRequest;

    @BeforeEach
    void setUp() {
        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");

        testReport = new Report();
        testReport.setId(1L);
        testReport.setReason("Inappropriate content");
        testReport.setComment("This product violates guidelines");
        testReport.setReporter(testUser);
        testReport.setCreatedAt(LocalDateTime.now());
        testReport.setSource(ReportSource.USER);

        testIncidence = new Incidence();
        testIncidence.setId(1L);
        testIncidence.setPublicUi(UUID.randomUUID());
        testIncidence.setProduct(testProduct);
        testIncidence.setStatus(IncidenceStatus.OPEN);
        testIncidence.setCreatedAt(LocalDateTime.now());
        testIncidence.setReports(Arrays.asList(testReport));

        testReportRequest = new ReportRequest("Inappropriate content", "This product violates guidelines");
        
        ProductResponse productResponse = new ProductResponse(1L, "Test Product", "Description", 99.99, 10, null, 4.5, Arrays.asList());
        testIncidenceResponse = new IncidenceResponse(
                1L, 
                UUID.randomUUID(), 
                productResponse, 
                IncidenceStatus.OPEN, 
                LocalDateTime.now(), 
                false, 
                null, 
                null, 
                IncidenceDecision.PENDING, 
                Arrays.asList()
        );
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /incidences/product/{productId} - Should create incidence")
    void testReportProduct() throws Exception {
        // Given
        when(incidenceMapper.toDomain(any(ReportRequest.class))).thenReturn(testReport);
        when(incidenceUseCasePort.createIncidence(eq(1L), any(Report.class), anyString())).thenReturn(testIncidence);
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(post("/incidences/product/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReportRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(incidenceUseCasePort, times(1)).createIncidence(eq(1L), any(Report.class), anyString());
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
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(incidenceUseCasePort, times(1)).getAllIncidences();
    }

    @Test
    @DisplayName("GET /incidences/{id} - Should return incidence when found")
    void testGetIncidence_Found() throws Exception {
        // Given
        when(incidenceUseCasePort.getIncidenceById(1L)).thenReturn(Optional.of(testIncidence));
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(get("/incidences/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(incidenceUseCasePort, times(1)).getIncidenceById(1L);
    }

    @Test
    @DisplayName("GET /incidences/{id} - Should return 404 when incidence not found")
    void testGetIncidence_NotFound() throws Exception {
        // Given
        when(incidenceUseCasePort.getIncidenceById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/incidences/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(incidenceUseCasePort, times(1)).getIncidenceById(999L);
    }

    @Test
    @WithMockUser(username = "moderator@example.com", roles = {"ADMIN"})
    @DisplayName("PUT /incidences/{id}/resolve - Should resolve incidence")
    void testResolveIncidence() throws Exception {
        // Given
        ResolveIncidenceRequest request = new ResolveIncidenceRequest(IncidenceDecision.MAINTAIN, "Reviewed and approved");
        when(incidenceUseCasePort.resolveIncidence(eq(1L), any(), anyString(), anyString()))
                .thenReturn(testIncidence);
        when(incidenceMapper.toResponse(testIncidence)).thenReturn(testIncidenceResponse);

        // When & Then
        mockMvc.perform(put("/incidences/{id}/resolve", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(incidenceUseCasePort, times(1)).resolveIncidence(eq(1L), any(), anyString(), anyString());
    }
}
