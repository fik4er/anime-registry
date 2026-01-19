package com.anime_registry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "genre")
@Getter
@Setter
public class Genre extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }
}