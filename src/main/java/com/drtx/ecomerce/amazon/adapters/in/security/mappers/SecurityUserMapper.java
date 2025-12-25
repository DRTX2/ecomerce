package com.drtx.ecomerce.amazon.adapters.in.security.mappers;

import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.core.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserMapper {
    public SecurityUserDetails toUserDetails(User user){
        return new SecurityUserDetails(user);
    }
}
