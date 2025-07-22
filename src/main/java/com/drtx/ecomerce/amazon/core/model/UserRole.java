package com.drtx.ecomerce.amazon.core.model;

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
