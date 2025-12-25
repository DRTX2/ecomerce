package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import com.drtx.ecomerce.amazon.adapters.out.persistence.incidence.IncidencePersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { IncidencePersistenceMapper.class, UserPersistenceMapper.class })
public interface AppealPersistenceMapper {
    Appeal toDomain(AppealEntity entity);

    AppealEntity toEntity(Appeal appeal);
}
