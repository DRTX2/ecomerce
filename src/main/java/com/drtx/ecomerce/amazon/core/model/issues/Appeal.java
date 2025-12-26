package com.drtx.ecomerce.amazon.core.model.issues;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;
// refactorizar
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

    public Appeal() {
    }

    public Appeal(Long id, Incidence incidence, User seller, String reason, LocalDateTime createdAt,
            AppealStatus status, User newModerator, AppealDecision finalDecision, LocalDateTime finalDecisionAt) {
        this.id = id;
        this.incidence = incidence;
        this.seller = seller;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = status;
        this.newModerator = newModerator;
        this.finalDecision = finalDecision;
        this.finalDecisionAt = finalDecisionAt;
    }

    public static AppealBuilder builder() {
        return new AppealBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incidence getIncidence() {
        return incidence;
    }

    public void setIncidence(Incidence incidence) {
        this.incidence = incidence;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AppealStatus getStatus() {
        return status;
    }

    public void setStatus(AppealStatus status) {
        this.status = status;
    }

    public User getNewModerator() {
        return newModerator;
    }

    public void setNewModerator(User newModerator) {
        this.newModerator = newModerator;
    }

    public AppealDecision getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(AppealDecision finalDecision) {
        this.finalDecision = finalDecision;
    }

    public LocalDateTime getFinalDecisionAt() {
        return finalDecisionAt;
    }

    public void setFinalDecisionAt(LocalDateTime finalDecisionAt) {
        this.finalDecisionAt = finalDecisionAt;
    }

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

    public static class AppealBuilder {
        private Long id;
        private Incidence incidence;
        private User seller;
        private String reason;
        private LocalDateTime createdAt;
        private AppealStatus status;
        private User newModerator;
        private AppealDecision finalDecision;
        private LocalDateTime finalDecisionAt;

        AppealBuilder() {
        }

        public AppealBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AppealBuilder incidence(Incidence incidence) {
            this.incidence = incidence;
            return this;
        }

        public AppealBuilder seller(User seller) {
            this.seller = seller;
            return this;
        }

        public AppealBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public AppealBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppealBuilder status(AppealStatus status) {
            this.status = status;
            return this;
        }

        public AppealBuilder newModerator(User newModerator) {
            this.newModerator = newModerator;
            return this;
        }

        public AppealBuilder finalDecision(AppealDecision finalDecision) {
            this.finalDecision = finalDecision;
            return this;
        }

        public AppealBuilder finalDecisionAt(LocalDateTime finalDecisionAt) {
            this.finalDecisionAt = finalDecisionAt;
            return this;
        }

        public Appeal build() {
            return new Appeal(id, incidence, seller, reason, createdAt, status, newModerator, finalDecision,
                    finalDecisionAt);
        }
    }
}
