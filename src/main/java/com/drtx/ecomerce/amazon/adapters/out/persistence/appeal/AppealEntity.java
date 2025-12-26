package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import com.drtx.ecomerce.amazon.adapters.out.persistence.incidence.IncidenceEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import com.drtx.ecomerce.amazon.core.model.issues.AppealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appeals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incidence_id", nullable = false)
    private IncidenceEntity incidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserEntity seller;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AppealStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_moderator_id")
    private UserEntity newModerator;

    @Column(name = "final_decision")
    @Enumerated(EnumType.STRING)
    private AppealDecision finalDecision;

    @Column(name = "final_decision_at")
    private LocalDateTime finalDecisionAt;

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (status == null) status = AppealStatus.PENDING;
        if (finalDecision == null) finalDecision = AppealDecision.PENDING;
    }
}
