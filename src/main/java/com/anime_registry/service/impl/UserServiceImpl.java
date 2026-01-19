package com.anime_registry.service.impl;

import com.anime_registry.views.ShowAnimeDto;
import com.anime_registry.entity.Anime;
import com.anime_registry.entity.Genre;
import com.anime_registry.entity.Subscription;
import com.anime_registry.entity.User;
import com.anime_registry.exception.AnimeNotFoundException;
import com.anime_registry.exception.UserNotFoundException;
import com.anime_registry.repository.AnimeRepository;
import com.anime_registry.repository.SubscriptionRepository;
import com.anime_registry.repository.UserRepository;
import com.anime_registry.service.UserService;
import com.anime_registry.views.UserProfileView;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AnimeRepository animeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AnimeRepository animeRepository, SubscriptionRepository subscriptionRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.animeRepository = animeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileView getUserProfile(String email) {
        log.debug("Получение профиля для пользователя: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<ShowAnimeDto> favoriteDtos = user.getFavorites().stream()
                .map(anime -> {
                    ShowAnimeDto dto = mapper.map(anime, ShowAnimeDto.class);
                    dto.setGenres(anime.getGenres().stream()
                            .map(Genre::getName)
                            .toList());
                    return dto;
                })
                .collect(Collectors.toList());
        Optional<Subscription> sub = subscriptionRepository.findByUserId(user.getId());

        UserProfileView view = new UserProfileView();
        view.setEmail(user.getEmail());
        view.setFavorites(favoriteDtos);
        view.setSubscribed(sub.isPresent() && sub.get().getIsActive());
        view.setSubscriptionScope(sub.isPresent() ? sub.get().getNotificationScope() : null);

        return view;
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#username")
    public void addFavoriteAnime(String username, Integer animeId) {
        log.debug("Добавление аниме {} в избранное для пользователя {}", animeId, username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new AnimeNotFoundException("Аниме не найдено: " + animeId));

        if (user.getFavorites().contains(anime)) {
            log.warn("Аниме {} уже в избранном у пользователя {}", animeId, username);
            return;
        }
        user.getFavorites().add(anime);
        userRepository.save(user);
        log.info("Аниме {} добавлено в избранное для пользователя {}", animeId, username);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#username")
    public void removeFavoriteAnime(String username, Integer animeId) {
        log.debug("Удаление аниме {} из избранного для пользователя {}", animeId, username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        user.getFavorites().removeIf(anime -> anime.getId().equals(animeId));

        userRepository.save(user);
        log.info("Аниме {} удалено из избранного для пользователя {}", animeId, username);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#username")
    public void subscribe(String username, String notificationScope) {
        log.debug("Подписка пользователя {} на уведомления: {}", username, notificationScope);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        Optional<Subscription> existingSubOpt = subscriptionRepository.findByUserId(user.getId());
        Subscription subscription;

        if (existingSubOpt.isPresent()) {
            subscription = existingSubOpt.get();
            subscription.setNotificationScope(notificationScope);
            subscription.setIsActive(true);
        } else {
            subscription = new Subscription();
            subscription.setUser(user);
            subscription.setNotificationScope(notificationScope);
            subscription.setIsActive(true);
        }

        subscriptionRepository.save(subscription);
        log.info("Пользователь {} успешно подписан на: {}", username, notificationScope);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#username")
    public void unsubscribe(String username) {
        log.debug("Отписка пользователя {}", username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        Optional<Subscription> subOpt = subscriptionRepository.findByUserId(user.getId());
        if (subOpt.isPresent()) {
            Subscription subscription = subOpt.get();
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
            log.info("Пользователь {} отписан", username);
        } else {
            log.warn("Пользователь {} не был подписан", username);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + email));
    }
}