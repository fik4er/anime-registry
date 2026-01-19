package com.anime_registry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionFormDto {
    @NotEmpty(message = "Тип уведомлений обязателен!")
    private String notificationScope;

    public SubscriptionFormDto() {}
}