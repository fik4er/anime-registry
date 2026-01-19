package com.anime_registry.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AnimeFormDto {
    private Integer id;
    @NotEmpty(message = "Название на русском обязательно!")
    @Size(min = 1, max = 255, message = "Название на русском должно быть от 1 до 255 символов!")
    private String titleRu;

    @Size(max = 255, message = "Название на японском должно быть до 255 символов!")
    private String titleJp;

    @Size(max = 255, message = "Название на английском должно быть до 255 символов!")
    private String titleEn;

    @NotNull(message = "Год выхода обязателен!")
    private Integer year;

    @NotEmpty(message = "Возрастной рейтинг обязателен!")
    private String ageRating;

    private Boolean isBanned = false;

    @Size(max = 255, message = "Путь к изображению должен быть до 255 символов!")
    private String thumbnailPath;

    private String description;

    @NotNull(message = "Жанры обязательны!")
    private List<String> genreNames;

    public AnimeFormDto() {}
}