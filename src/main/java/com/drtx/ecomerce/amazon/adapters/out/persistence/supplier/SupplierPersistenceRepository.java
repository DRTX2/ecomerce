package com.drtx.ecomerce.amazon.adapters.out.persistence.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierPersistenceRepository extends JpaRepository<SupplierEntity, Long> {
}
