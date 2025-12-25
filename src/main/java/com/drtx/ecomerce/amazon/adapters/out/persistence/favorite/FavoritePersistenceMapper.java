package com.drtx.ecomerce.amazon.adapters.out.persistence.favorite;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductPersistenceMapper.class, UserPersistenceMapper.class})
public interface FavoritePersistenceMapper {
    Favorite toDomain(FavoriteEntity entity);
    FavoriteEntity toEntity(Favorite favorite);
    List<Favorite> toDomainList(List<FavoriteEntity> entities);
}
