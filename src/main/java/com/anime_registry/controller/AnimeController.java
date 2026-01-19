package com.anime_registry.controller;

import com.anime_registry.dto.AnimeFormDto;
import com.anime_registry.views.ShowAnimeDto;
import com.anime_registry.views.ShowDetailedAnimeDto;
import com.anime_registry.service.AnimeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/anime")
public class AnimeController {

    private final AnimeService animeService;

    @Autowired
    public AnimeController(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GetMapping("/all")
    public String showAllAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String ageRating,
            @RequestParam(required = false) String genre,
            Model model) {

        log.debug("Отображение списка аниме: страница={}, размер={}, поиск={}, banned={}, year={}, ageRating={}, genre={}",
                page, size, search, banned, year, ageRating, genre);

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("animes", animeService.searchAnimes(search));
            model.addAttribute("search", search);
        } else {
            boolean hasFilters = banned != null || year != null || ageRating != null || (genre != null && !genre.isEmpty());
            if (hasFilters) {
                List<ShowAnimeDto> filteredAnimes = animeService.filterAnimes(banned, year, ageRating, genre);
                model.addAttribute("animes", filteredAnimes);
                model.addAttribute("currentPage", 0);
                model.addAttribute("totalPages", 1);
                model.addAttribute("totalItems", filteredAnimes.size());
            } else {
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
                Page<ShowAnimeDto> animePage = animeService.getAllAnimesPaginated(pageable);
                model.addAttribute("animes", animePage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", animePage.getTotalPages());
                model.addAttribute("totalItems", animePage.getTotalElements());
            }
        }
        model.addAttribute("bannedFilter", banned);
        model.addAttribute("yearFilter", year);
        model.addAttribute("ageRatingFilter", ageRating);
        model.addAttribute("genreFilter", genre);
        model.addAttribute("allGenres", animeService.getAllGenres());
        model.addAttribute("allAgeRatings", List.of("0+", "6+", "12+", "16+", "18+"));
        return "anime-list";
    }

    @GetMapping("/details/{id}")
    public String showAnimeDetails(@PathVariable("id") Integer id, Model model) {
        log.debug("Запрос деталей аниме с ID: {}", id);
        ShowDetailedAnimeDto details = animeService.getAnimeDetails(id);
        model.addAttribute("animeDetails", details);
        return "anime-details";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        log.debug("Отображение формы добавления аниме");
        model.addAttribute("animeForm", new AnimeFormDto());
        model.addAttribute("existingGenres", animeService.getAllGenres());
        return "anime-add";
    }

    @ModelAttribute("animeForm")
    public AnimeFormDto initAnimeForm() {
        return new AnimeFormDto();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAnime(@Valid @ModelAttribute("animeForm") AnimeFormDto animeForm,
                           BindingResult bindingResult,
                           @RequestParam(value = "newGenres", required = false) String newGenres,
                           RedirectAttributes redirectAttributes) {
        log.debug("Обработка POST запроса на добавление аниме: {}", animeForm.getTitleRu());
        
        if (newGenres != null && !newGenres.trim().isEmpty()) {
            String[] newGenreArray = newGenres.split(",");
            for (String genre : newGenreArray) {
                String trimmedGenre = genre.trim();
                if (!trimmedGenre.isEmpty() && !animeForm.getGenreNames().contains(trimmedGenre)) {
                    animeForm.getGenreNames().add(trimmedGenre);
                }
            }
        }
        
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("animeForm", animeForm);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.animeForm",
                    bindingResult);
            return "redirect:/anime/add";
        }
        animeService.addAnime(animeForm);
        redirectAttributes.addFlashAttribute("successMessage", "Аниме '" + animeForm.getTitleRu() + "' успешно добавлено!");
        return "redirect:/anime/all";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        log.debug("Отображение формы редактирования аниме с ID: {}", id);
        ShowDetailedAnimeDto anime = animeService.getAnimeDetails(id);
        AnimeFormDto animeForm = convertToShowDetailedAnimeDtoToAnimeFormDto(anime);
        model.addAttribute("animeForm", animeForm);
        model.addAttribute("existingGenres", animeService.getAllGenres());
        return "anime-add";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editAnime(@PathVariable("id") Integer id,
                           @Valid @ModelAttribute("animeForm") AnimeFormDto animeForm,
                           BindingResult bindingResult,
                           @RequestParam(value = "newGenres", required = false) String newGenres,
                           RedirectAttributes redirectAttributes) {
        log.debug("Обработка POST запроса на редактирование аниме с ID {}: {}", id, animeForm.getTitleRu());
        
        if (newGenres != null && !newGenres.trim().isEmpty()) {
            String[] newGenreArray = newGenres.split(",");
            for (String genre : newGenreArray) {
                String trimmedGenre = genre.trim();
                if (!trimmedGenre.isEmpty() && !animeForm.getGenreNames().contains(trimmedGenre)) {
                    animeForm.getGenreNames().add(trimmedGenre);
                }
            }
        }
        
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("animeForm", animeForm);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.animeForm",
                    bindingResult);
            return "redirect:/anime/edit/" + id;
        }
        animeService.updateAnime(id, animeForm);
        redirectAttributes.addFlashAttribute("successMessage", "Аниме '" + animeForm.getTitleRu() + "' успешно обновлено!");
        return "redirect:/anime/details/" + id;
    }

    private AnimeFormDto convertToShowDetailedAnimeDtoToAnimeFormDto(ShowDetailedAnimeDto dto) {
        AnimeFormDto formDto = new AnimeFormDto();
        formDto.setId(dto.getId());
        formDto.setTitleRu(dto.getTitleRu());
        formDto.setTitleJp(dto.getTitleJp());
        formDto.setTitleEn(dto.getTitleEn());
        formDto.setYear(dto.getYear());
        formDto.setAgeRating(dto.getAgeRating());
        formDto.setIsBanned(dto.getIsBanned());
        formDto.setThumbnailPath(dto.getThumbnailPath());
        formDto.setDescription(dto.getDescription());
        formDto.setGenreNames(dto.getGenres());
        return formDto;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAnime(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        log.debug("Запрос на удаление аниме с ID: {}", id);
        animeService.deleteAnime(id);
        redirectAttributes.addFlashAttribute("successMessage", "Аниме с ID " + id + " успешно удалено!");
        return "redirect:/anime/all";
    }
}