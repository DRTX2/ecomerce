package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.core.model.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
