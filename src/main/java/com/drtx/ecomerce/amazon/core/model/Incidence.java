package com.drtx.ecomerce.amazon.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Incidence {
    private Long id;
    private UUID publicUi;
    private Product product;
    private IncidenceStatus status;
    private LocalDateTime createdAt;
    private Boolean autoclosed;
    private User moderator;
    private String moderatorComment;
    private IncidenceDecision decision;
    private List<Report> reports;

    public void initializeDefaults() {
        if (this.publicUi == null) {
            this.publicUi = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = IncidenceStatus.OPEN;
        }
        if (this.decision == null) {
            this.decision = IncidenceDecision.PENDING;
        }
        if (this.autoclosed == null) {
            this.autoclosed = false;
        }
        if (this.reports == null) {
            this.reports = new ArrayList<>();
        }
    }
}
