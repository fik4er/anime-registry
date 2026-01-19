package com.anime_registry.views;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ShowDetailedAnimeDto implements Serializable {
    private Integer id;
    private String titleRu;
    private String titleJp;
    private String titleEn;
    private Integer year;
    private String ageRating;
    private Boolean isBanned;
    private String thumbnailPath;
    private String description;
    private List<String> genres;
    private List<ShowBanRecordDto> banRecords;
    private Double probabilityOfBlock;
    public ShowDetailedAnimeDto() {}
}