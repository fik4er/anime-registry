package com.anime_registry.controller;

import com.anime_registry.views.ShowAnimeDto;
import com.anime_registry.service.AnimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Slf4j
@Controller
public class HomeController {
    private final AnimeService animeService;

    @Autowired
    public HomeController(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        log.debug("Отображение главной страницы");
        List<ShowAnimeDto> latestBannedAnimes = animeService.getAllAnimes().stream()
                .filter(ShowAnimeDto::getIsBanned)
                .limit(10)
                .toList();
        model.addAttribute("latestBannedAnimes", latestBannedAnimes);
        return "index";
    }
}

