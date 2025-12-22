package com.drtx.ecomerce.amazon.adapters.out.persistence.review;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserPersistenceMapper.class, ProductPersistenceMapper.class })
public interface ReviewPersistenceMapper {
    Review toDomain(ReviewEntity entity);

    List<Review> toDomainList(List<ReviewEntity> entities);

    ReviewEntity toEntity(Review domain);
}
