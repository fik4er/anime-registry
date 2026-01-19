package com.anime_registry.security;

import com.anime_registry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Загрузка пользователя по email: {}", email);
        com.anime_registry.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        log.info("Найден пользователь: {}, хешированный пароль: {}", user.getEmail(), user.getPasswordHash());

        return new UserPrincipal(user);
    }
}