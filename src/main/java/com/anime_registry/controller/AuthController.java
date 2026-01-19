package com.anime_registry.controller;

import com.anime_registry.dto.UserRegistrationDto;
import com.anime_registry.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
        log.info("AuthController инициализирован");
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto initRegistrationForm() {
        return new UserRegistrationDto();
    }

    @GetMapping("/register")
    public String registerPage() {
        log.debug("Отображение страницы регистрации");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.debug("Обработка регистрации пользователя: {}", registrationDto.getEmail());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при регистрации: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("userRegistrationDto", registrationDto);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userRegistrationDto",
                    bindingResult);
            return "redirect:/users/register";
        }

        try {
            authService.register(registrationDto);
            log.info("Пользователь успешно зарегистрирован: {}", registrationDto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Пожалуйста, войдите.");
            return "redirect:/users/login";
        } catch (RuntimeException e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("userRegistrationDto", registrationDto);
            return "redirect:/users/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        log.debug("Отображение страницы входа");
        if (error != null) {
            model.addAttribute("errorMessage", "Неверный email или пароль.");
        }
        return "login";
    }

    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        log.debug("Отображение профиля пользователя: {}", principal.getName());
        model.addAttribute("username", principal.getName());
        return "profile";
    }
}