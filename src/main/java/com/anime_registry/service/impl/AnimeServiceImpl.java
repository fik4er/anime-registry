package com.anime_registry.service.impl;

import com.anime_registry.dto.*;
import com.anime_registry.entity.Anime;
import com.anime_registry.entity.Genre;
import com.anime_registry.exception.AnimeNotFoundException;
import com.anime_registry.repository.AnimeRepository;
import com.anime_registry.repository.BanRecordRepository;
import com.anime_registry.repository.GenreRepository;
import com.anime_registry.service.AnimeService;
import com.anime_registry.views.ShowAnimeDto;
import com.anime_registry.views.ShowBanRecordDto;
import com.anime_registry.views.ShowDetailedAnimeDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AnimeServiceImpl implements AnimeService {

    private final AnimeRepository animeRepository;
    private final GenreRepository genreRepository;
    private final BanRecordRepository banRecordRepository;
    private final ModelMapper mapper;

    @Autowired
    public AnimeServiceImpl(AnimeRepository animeRepository, GenreRepository genreRepository, BanRecordRepository banRecordRepository, ModelMapper mapper) {
        this.animeRepository = animeRepository;
        this.genreRepository = genreRepository;
        this.banRecordRepository = banRecordRepository;
        this.mapper = mapper;
    }

    @Override
    public List<String> getAllGenres() {
        log.debug("Получение всех жанров");
        return genreRepository.findAll().stream()
                .map(Genre::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"animes", "anime"}, allEntries = true)
    public void addAnime(AnimeFormDto animeFormDto) {
        log.debug("Добавление нового аниме: {}", animeFormDto.getTitleRu());

        Anime anime = mapper.map(animeFormDto, Anime.class);

        if (animeFormDto.getGenreNames() != null && !animeFormDto.getGenreNames().isEmpty()) {
            List<Genre> genres = animeFormDto.getGenreNames().stream()
                    .map(name -> {
                        return genreRepository.findByName(name)
                                .orElseGet(() -> {
                                    log.info("Создание нового жанра: {}", name);
                                    Genre newGenre = new Genre(name);
                                    return genreRepository.save(newGenre);
                                });
                    })
                    .collect(Collectors.toList());
            anime.getGenres().clear();
            anime.getGenres().addAll(genres);
        }

        animeRepository.save(anime);
        log.info("Аниме успешно добавлено: {}", animeFormDto.getTitleRu());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"animes", "anime"}, allEntries = true)
    public void updateAnime(Integer id, AnimeFormDto animeFormDto) {
        log.debug("Обновление аниме с ID: {}", id);
        Anime existingAnime = animeRepository.findById(id)
                .orElseThrow(() -> new AnimeNotFoundException("Аниме не найдено: " + id));

        existingAnime.setTitleRu(animeFormDto.getTitleRu());
        existingAnime.setTitleJp(animeFormDto.getTitleJp());
        existingAnime.setTitleEn(animeFormDto.getTitleEn());
        existingAnime.setYear(animeFormDto.getYear());
        existingAnime.setAgeRating(animeFormDto.getAgeRating());
        existingAnime.setIsBanned(animeFormDto.getIsBanned());
        existingAnime.setThumbnailPath(animeFormDto.getThumbnailPath());
        existingAnime.setDescription(animeFormDto.getDescription());

        if (animeFormDto.getGenreNames() != null) {
            List<Genre> genres = animeFormDto.getGenreNames().stream()
                    .map(name -> {
                        return genreRepository.findByName(name)
                                .orElseGet(() -> {
                                    log.info("Создание нового жанра: {}", name);
                                    Genre newGenre = new Genre(name);
                                    return genreRepository.save(newGenre);
                                });
                    })
                    .collect(Collectors.toList());
            existingAnime.getGenres().clear();
            existingAnime.getGenres().addAll(genres);
        }

        animeRepository.save(existingAnime);
        log.info("Аниме успешно обновлено: {}", existingAnime.getTitleRu());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"animes", "anime"}, allEntries = true)
    public void deleteAnime(Integer id) {
        log.debug("Удаление аниме с ID: {}", id);
        if (!animeRepository.existsById(id)) {
            throw new RuntimeException("Аниме не найдено для удаления: " + id);
        }
        animeRepository.deleteById(id);
        log.info("Аниме с ID {} успешно удалено", id);
    }

    @Override
    @Cacheable(value = "animes", key = "'all'")
    public List<ShowAnimeDto> getAllAnimes() {
        log.debug("Получение списка всех аниме");
        return animeRepository.findAllWithGenres().stream()
                .map(anime -> {
                    ShowAnimeDto dto = mapper.map(anime, ShowAnimeDto.class);
                    List<String> genreNames = anime.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toList());
                    dto.setGenres(genreNames);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowAnimeDto> getAllAnimesPaginated(Pageable pageable) {
        log.debug("Получение списка аниме с пагинацией: страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());
        return animeRepository.findAllWithGenres(pageable)
                .map(anime -> {
                    ShowAnimeDto dto = mapper.map(anime, ShowAnimeDto.class);
                    List<String> genreNames = anime.getGenres().stream()
                            .map(Genre::getName)
                            .collect(Collectors.toList());
                    dto.setGenres(genreNames);
                    return dto;
                });
    }

    @Override
    public List<ShowAnimeDto> searchAnimes(String searchTerm) {
        log.debug("Поиск аниме по запросу: {}", searchTerm);
        return animeRepository.findByTitleRuContainingIgnoreCaseOrTitleEnContainingIgnoreCaseWithGenres(searchTerm).stream()
                .map(anime -> {
                    ShowAnimeDto dto = mapper.map(anime, ShowAnimeDto.class);
                    List<String> genreNames = anime.getGenres().stream()
                            .map(com.anime_registry.entity.Genre::getName)
                            .collect(Collectors.toList());
                    dto.setGenres(genreNames);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ShowDetailedAnimeDto getAnimeDetails(Integer id) {
        log.debug("Получение деталей аниме с ID: {}", id);
        Anime anime = animeRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new AnimeNotFoundException("Аниме с ID " + id + " не найдено")); // Изменено

        ShowDetailedAnimeDto detailedDto = mapper.map(anime, ShowDetailedAnimeDto.class);

        List<String> genreNames = anime.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
        detailedDto.setGenres(genreNames);

        List<ShowBanRecordDto> banRecords = anime.getBanRecords().stream()
                .map(ban -> mapper.map(ban, ShowBanRecordDto.class))
                .collect(Collectors.toList());
        detailedDto.setBanRecords(banRecords);

        Double probability = 0.0;
        if (!genreNames.isEmpty()) {
            probability = genreNames.stream()
                    .map(genreName -> calculateProbabilityOfBlock(genreName, anime.getAgeRating()))
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
        } else {
            probability = calculateProbabilityOfBlock(null, anime.getAgeRating());
        }
        detailedDto.setProbabilityOfBlock(probability);

        return detailedDto;
    }

    @Override
    public List<ShowAnimeDto> filterAnimes(Boolean isBanned, Integer year, String ageRating, String genreName) {
        String fRating = (ageRating == null || ageRating.isBlank()) ? null : ageRating;
        String fGenre = (genreName == null || genreName.isBlank()) ? null : genreName;

        List<Anime> allAnimes = animeRepository.findAllWithGenres();

        return allAnimes.stream()
                .filter(a -> isBanned == null || a.getIsBanned().equals(isBanned))
                .filter(a -> year == null || a.getYear().equals(year))
                .filter(a -> fRating == null || a.getAgeRating().equalsIgnoreCase(fRating))
                .filter(a -> fGenre == null || a.getGenres().stream().anyMatch(g -> g.getName().equalsIgnoreCase(fGenre)))
                .map(a -> {
                    ShowAnimeDto dto = mapper.map(a, ShowAnimeDto.class);
                    dto.setGenres(a.getGenres().stream().map(Genre::getName).toList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateProbabilityOfBlock(String genreName, String ageRating) {
        log.debug("Расчет вероятности блокировки для жанра: {}, рейтинга: {}", genreName, ageRating);
        if (genreName == null || ageRating == null) {
            log.warn("genreName или ageRating равны null, вероятность 0");
            return 0.0;
        }
        long totalAnimes = animeRepository.countByGenreAndAgeRating(genreName, ageRating);
        long bannedAnimes = animeRepository.countByGenreAndAgeRatingAndIsBanned(genreName, ageRating, true);
        if (totalAnimes == 0) {
            return 0.0;
        }
        return ((double) bannedAnimes / totalAnimes) * 100.0;
    }
}
