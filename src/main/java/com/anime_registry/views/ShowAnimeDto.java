package com.anime_registry.views;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ShowAnimeDto implements Serializable {
    private Integer id;
    private String titleRu;
    private String titleEn;
    private String thumbnailPath;
    private String ageRating;
    private Boolean isBanned;
    private List<String> genres;
    private Integer year;
    public ShowAnimeDto() {}
}