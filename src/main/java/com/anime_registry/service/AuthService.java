package com.anime_registry.service;

import com.anime_registry.dto.UserRegistrationDto;

public interface AuthService {
    void register(UserRegistrationDto registrationDto);
    boolean userExists(String email);
}