package com.anime_registry.service;

import com.anime_registry.dto.*;
import com.anime_registry.views.ShowAnimeDto;
import com.anime_registry.views.ShowDetailedAnimeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnimeService {

    void addAnime(AnimeFormDto animeFormDto);
    void updateAnime(Integer id, AnimeFormDto animeFormDto);
    void deleteAnime(Integer id);

    List<ShowAnimeDto> getAllAnimes();
    Page<ShowAnimeDto> getAllAnimesPaginated(Pageable pageable);
    List<ShowAnimeDto> searchAnimes(String searchTerm);
    ShowDetailedAnimeDto getAnimeDetails(Integer id);

    List<ShowAnimeDto> filterAnimes(Boolean isBanned, Integer year, String ageRating, String genreName);

    List<String> getAllGenres();

    Double calculateProbabilityOfBlock(String genreName, String ageRating);
}