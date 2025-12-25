package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductPersistenceMapper.class, UserPersistenceMapper.class})
public interface IncidencePersistenceMapper {

    @Mapping(target = "reports", source = "reports")
    Incidence toDomain(IncidenceEntity entity);

    @Mapping(target = "reports", source = "reports")
    IncidenceEntity toEntity(Incidence incidence);

    Report toDomain(ReportEntity entity);
    ReportEntity toEntity(Report report);
    
    List<Incidence> toDomainList(List<IncidenceEntity> entities);
    List<IncidenceEntity> toEntityList(List<Incidence> incidences);
}
