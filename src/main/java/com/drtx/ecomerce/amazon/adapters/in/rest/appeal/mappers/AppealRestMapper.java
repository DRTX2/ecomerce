package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { IncidenceRestMapper.class, UserRestMapper.class })
public interface AppealRestMapper {
    AppealResponse toResponse(Appeal appeal);
}
