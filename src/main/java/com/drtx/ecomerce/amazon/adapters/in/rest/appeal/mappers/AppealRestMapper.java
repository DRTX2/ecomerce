package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { IncidenceRestMapper.class, UserRestMapper.class })
public interface AppealRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "incidence", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "newModerator", ignore = true)
    @Mapping(target = "finalDecision", ignore = true)
    Appeal toDomain(AppealRequest request);

    AppealResponse toResponse(Appeal appeal);
}
