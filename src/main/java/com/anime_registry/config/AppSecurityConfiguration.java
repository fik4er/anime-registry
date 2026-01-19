package com.anime_registry.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppSecurityConfiguration {

    private final UserDetailsService userDetailsService;

    public AppSecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/anime/**", "/users/login", "/users/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/anime/add", "/anime/delete/**").hasRole(UserRoles.ADMIN.name())
                        .requestMatchers("/profile", "/subscribe", "/users/profile").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/users/login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(userDetailsService)
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}