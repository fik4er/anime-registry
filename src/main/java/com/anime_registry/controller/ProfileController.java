package com.anime_registry.controller;

import com.anime_registry.service.UserService;
import com.anime_registry.views.UserProfileView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        log.debug("Запрос профиля для: {}", username);
        UserProfileView userProfile = userService.getUserProfile(username);
        model.addAttribute("userProfile", userProfile);
        return "profile";
    }


    @PostMapping("/favorites/add/{animeId}")
    public String addFavorite(@PathVariable("animeId") Integer animeId, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        log.debug("Добавление аниме {} в избранное для {}", animeId, username);
        try {
            userService.addFavoriteAnime(username, animeId);
            redirectAttributes.addFlashAttribute("successMessage", "Аниме добавлено в избранное!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/anime/details/" + animeId;
    }

    @PostMapping("/favorites/remove/{animeId}")
    public String removeFavorite(@PathVariable("animeId") Integer animeId, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        log.debug("Удаление аниме {} из избранного для {}", animeId, username);
        userService.removeFavoriteAnime(username, animeId);
        redirectAttributes.addFlashAttribute("successMessage", "Аниме удалено из избранного!");
        return "redirect:/profile";
    }
}