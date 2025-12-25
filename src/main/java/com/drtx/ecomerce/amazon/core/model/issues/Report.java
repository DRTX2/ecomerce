package com.drtx.ecomerce.amazon.core.model.issues;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;

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

    public static ReportBuilder builder() {
        return new ReportBuilder();
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

    public static class ReportBuilder {
        private Long id;
        private User reporter;
        private String reason;
        private String comment;
        private LocalDateTime createdAt;
        private ReportSource source;

        ReportBuilder() {
        }

        public ReportBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReportBuilder reporter(User reporter) {
            this.reporter = reporter;
            return this;
        }

        public ReportBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ReportBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ReportBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReportBuilder source(ReportSource source) {
            this.source = source;
            return this;
        }

        public Report build() {
            return new Report(id, reporter, reason, comment, createdAt, source);
        }
    }
}
