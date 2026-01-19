package com.anime_registry.service.impl;

import com.anime_registry.config.UserRoles;
import com.anime_registry.dto.UserRegistrationDto;
import com.anime_registry.entity.Role;
import com.anime_registry.entity.User;
import com.anime_registry.repository.RoleRepository;
import com.anime_registry.repository.UserRepository;
import com.anime_registry.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        log.debug("Регистрация нового пользователя: {}", registrationDto.getEmail());

        Role userRole = roleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена!"));

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setIsEmailVerified(true);
        user.getRoles().add(userRole);

        userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {}", registrationDto.getEmail());
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}