package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.core.model.order.OrderSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {
    public static Specification<OrderEntity> withCriteria(OrderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            criteria.userUuid().ifPresent(userUuid ->
                    predicates.getExpressions().add(
                            criteriaBuilder.equal(root.get("user").get("uuid"), userUuid)
                    )
            );

            criteria.orderState().ifPresent(orderState ->
                    predicates.getExpressions().add(
                            criteriaBuilder.equal(root.get("orderState"), orderState)
                    )
            );

            if (criteria.from() != null && criteria.to() != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.between(root.get("createdAt"), criteria.from(), criteria.to())
                );
            }

            if (criteria.minTotal() != null && criteria.maxTotal() != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.between(root.get("total"), criteria.minTotal(), criteria.maxTotal())
                );
            }

            return predicates;
        };
    }
}
