package com.anime_registry.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AnimeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAnimeNotFound(AnimeNotFoundException ex, Model model) {
        log.warn("Аниме не найдено: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Аниме не найдено");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/custom-error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        log.warn("Пользователь не найден: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Пользователь не найден");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/custom-error";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericRuntimeException(RuntimeException ex, Model model) {
        log.error("Внутренняя ошибка сервера", ex);
        model.addAttribute("errorTitle", "Внутренняя ошибка сервера");
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка");
        model.addAttribute("errorCode", "500");
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Необработанное исключение", ex);
        model.addAttribute("errorTitle", "Ошибка сервера");
        model.addAttribute("errorMessage", "Произошла ошибка");
        model.addAttribute("errorCode", "500");
        return "error/custom-error";
    }
}
