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
public class Appeal {
    private Long id;
    private Incidence incidence;
    private User seller;
    private String reason;
    private LocalDateTime createdAt;
    private AppealStatus status;
    private User newModerator;
    private AppealDecision finalDecision;
    private LocalDateTime finalDecisionAt;

    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = AppealStatus.PENDING;
        }
        if (this.finalDecision == null) {
            this.finalDecision = AppealDecision.PENDING;
        }
    }
}
