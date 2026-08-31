package com.drtx.ecomerce.amazon.adapters.out.persistence.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface AuditLogPersistenceRepository extends JpaRepository<AuditLogEntity, UUID> {
}
