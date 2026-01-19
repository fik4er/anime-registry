package com.anime_registry.repository;

import com.anime_registry.config.UserRoles;
import com.anime_registry.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(UserRoles name);
}