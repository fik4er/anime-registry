package com.anime_registry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscription")
@Getter
@Setter
public class Subscription extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "notification_scope", nullable = false)
    private String notificationScope;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Subscription() {}
}