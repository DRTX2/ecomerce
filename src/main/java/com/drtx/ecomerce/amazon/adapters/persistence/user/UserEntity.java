package com.drtx.ecomerce.amazon.adapters.persistence.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name ;

    @Column(nullable=false, unique = true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private String phone;

    @Column(nullable=false)
    private String role;
}
