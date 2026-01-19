package com.anime_registry.service;

import com.anime_registry.views.UserProfileView;

public interface UserService {
    UserProfileView getUserProfile(String username);
    void addFavoriteAnime(String username, Integer animeId);
    void removeFavoriteAnime(String username, Integer animeId);
    void subscribe(String username, String notificationScope);
    void unsubscribe(String username);
    com.anime_registry.entity.User findUserByEmail(String email);
}