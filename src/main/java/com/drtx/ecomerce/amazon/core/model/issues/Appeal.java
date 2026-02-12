package com.drtx.ecomerce.amazon.core.model.issues;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class Appeal {
    private Long id;
    private UUID uuid;
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

    public Appeal(Long id, UUID uuid, Incidence incidence, User seller, String reason, LocalDateTime createdAt,
            AppealStatus status, User newModerator, AppealDecision finalDecision, LocalDateTime finalDecisionAt) {
        this.id = id;
        this.uuid = uuid;
        this.incidence = incidence;
        this.seller = seller;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = status;
        this.newModerator = newModerator;
        this.finalDecision = finalDecision;
        this.finalDecisionAt = finalDecisionAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
}
