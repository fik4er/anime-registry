package com.anime_registry.dto;

import com.anime_registry.utils.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    @NotEmpty(message = "Email обязателен!")
    @Email(message = "Введите корректный email!")
    @UniqueEmail
    private String email;

    @NotEmpty(message = "Пароль обязателен!")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов!")
    private String password;

    @NotEmpty(message = "Подтверждение пароля обязательно!")
    private String confirmPassword;

    public UserRegistrationDto() {}
}

