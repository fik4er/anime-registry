package com.anime_registry.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "anime")
@Getter
@Setter
public class Anime extends BaseEntity {
    @Column(name = "title_ru")
    private String titleRu;

    @Column(name = "title_jp")
    private String titleJp;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "year")
    private Integer year;

    @Column(name = "age_rating")
    private String ageRating;

    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BanRecord> banRecords = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "anime_genre",
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )

    private List<Genre> genres = new ArrayList<>();
    public Anime() {}
}