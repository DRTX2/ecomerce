package com.drtx.ecomerce.amazon.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
    private Long id;
    private User reporter; // usuario que reporta
    private String reason; // motivo del reporte
    private String comment; // comentario adicional (opcional)
    private LocalDateTime createdAt;
    private ReportSource source;

    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.source == null) {
            this.source = ReportSource.USER;
        }
    }
}
