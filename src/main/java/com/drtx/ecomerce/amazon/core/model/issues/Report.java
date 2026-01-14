package com.drtx.ecomerce.amazon.core.model.issues;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;

/**
 * Modelo de dominio para reportes.
 * Simplificado: usa constructor vacío + setters en lugar de builder.
 */
public class Report {
    private Long id;
    private User reporter;
    private String reason;
    private String comment;
    private LocalDateTime createdAt;
    private ReportSource source;

    public Report() {
    }

    public Report(Long id, User reporter, String reason, String comment, LocalDateTime createdAt, ReportSource source) {
        this.id = id;
        this.reporter = reporter;
        this.reason = reason;
        this.comment = comment;
        this.createdAt = createdAt;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReportSource getSource() {
        return source;
    }

    public void setSource(ReportSource source) {
        this.source = source;
    }

    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.source == null) {
            this.source = ReportSource.USER;
        }
    }
}
