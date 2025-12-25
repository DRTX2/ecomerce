package com.drtx.ecomerce.amazon.core.model.user;

public enum UserRole {
    USER,
    ADMIN,
    MODERATOR;

    public String getAuthority(){
        return "ROLE_"+this.name();
    }

    public String roleName(){
        return this.name();
    }
}
