package com.drtx.ecomerce.amazon.adapters.in.rest.favorite.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.dto.FavoriteResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductRestMapper.class, UserRestMapper.class})
public interface FavoriteRestMapper {
    FavoriteResponse toResponse(Favorite favorite);
}
