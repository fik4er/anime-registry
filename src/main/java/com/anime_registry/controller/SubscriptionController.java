package com.anime_registry.controller;

import com.anime_registry.dto.SubscriptionFormDto;
import com.anime_registry.service.UserService;
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
@RequestMapping("/subscribe")
public class SubscriptionController {

    private final UserService userService;

    @Autowired
    public SubscriptionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showSubscriptionForm(Principal principal, Model model) {
        log.debug("Отображение формы подписки для {}", principal.getName());
        model.addAttribute("subscriptionForm", new SubscriptionFormDto());
        return "subscription";
    }

    @ModelAttribute("subscriptionForm")
    public SubscriptionFormDto initSubscriptionForm() {
        return new SubscriptionFormDto();
    }

    @PostMapping
    public String handleSubscription(@Valid @ModelAttribute("subscriptionForm") SubscriptionFormDto subscriptionForm,
                                     BindingResult bindingResult,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        log.debug("Обработка подписки для {}: {}", username, subscriptionForm.getNotificationScope());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации подписки: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("subscriptionForm", subscriptionForm);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.subscriptionForm",
                    bindingResult);
            return "redirect:/subscribe";
        }

        userService.subscribe(username, subscriptionForm.getNotificationScope());
        redirectAttributes.addFlashAttribute("successMessage", "Подписка оформлена успешно!");
        return "redirect:/profile";
    }

    @PostMapping("/unsubscribe")
    public String handleUnsubscribe(Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        log.debug("Обработка отписки для {}", username);
        userService.unsubscribe(username);
        redirectAttributes.addFlashAttribute("successMessage", "Подписка отменена.");
        return "redirect:/profile";
    }
}