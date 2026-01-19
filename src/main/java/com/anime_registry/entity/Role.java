package com.anime_registry.entity;
import com.anime_registry.config.UserRoles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private UserRoles name;

    public Role() {}

    public Role(UserRoles name) {
        this.name = name;
    }
}
