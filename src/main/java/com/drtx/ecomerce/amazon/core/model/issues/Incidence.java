package com.drtx.ecomerce.amazon.core.model.issues;

import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Incidence() {
    }

    public Incidence(Long id, UUID publicUi, Product product, IncidenceStatus status, LocalDateTime createdAt,
            Boolean autoclosed, User moderator, String moderatorComment, IncidenceDecision decision,
            List<Report> reports) {
        this.id = id;
        this.publicUi = publicUi;
        this.product = product;
        this.status = status;
        this.createdAt = createdAt;
        this.autoclosed = autoclosed;
        this.moderator = moderator;
        this.moderatorComment = moderatorComment;
        this.decision = decision;
        this.reports = reports;
    }

    public static IncidenceBuilder builder() {
        return new IncidenceBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicUi() {
        return publicUi;
    }

    public void setPublicUi(UUID publicUi) {
        this.publicUi = publicUi;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public IncidenceStatus getStatus() {
        return status;
    }

    public void setStatus(IncidenceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getAutoclosed() {
        return autoclosed;
    }

    public void setAutoclosed(Boolean autoclosed) {
        this.autoclosed = autoclosed;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public String getModeratorComment() {
        return moderatorComment;
    }

    public void setModeratorComment(String moderatorComment) {
        this.moderatorComment = moderatorComment;
    }

    public IncidenceDecision getDecision() {
        return decision;
    }

    public void setDecision(IncidenceDecision decision) {
        this.decision = decision;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

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

    public static class IncidenceBuilder {
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

        IncidenceBuilder() {
        }

        public IncidenceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IncidenceBuilder publicUi(UUID publicUi) {
            this.publicUi = publicUi;
            return this;
        }

        public IncidenceBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public IncidenceBuilder status(IncidenceStatus status) {
            this.status = status;
            return this;
        }

        public IncidenceBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IncidenceBuilder autoclosed(Boolean autoclosed) {
            this.autoclosed = autoclosed;
            return this;
        }

        public IncidenceBuilder moderator(User moderator) {
            this.moderator = moderator;
            return this;
        }

        public IncidenceBuilder moderatorComment(String moderatorComment) {
            this.moderatorComment = moderatorComment;
            return this;
        }

        public IncidenceBuilder decision(IncidenceDecision decision) {
            this.decision = decision;
            return this;
        }

        public IncidenceBuilder reports(List<Report> reports) {
            this.reports = reports;
            return this;
        }

        public Incidence build() {
            return new Incidence(id, publicUi, product, status, createdAt, autoclosed, moderator, moderatorComment,
                    decision, reports);
        }
    }
}
