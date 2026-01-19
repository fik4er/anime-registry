package com.anime_registry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "anime_id")
    )
    private List<Anime> favorites = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;

    public User() {}

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public List<Integer> getFavoriteAnimeIds() {
        if (this.favorites == null) {
            return List.of();
        }
        return this.favorites.stream()
                .map(Anime::getId)
                .toList();
    }
}