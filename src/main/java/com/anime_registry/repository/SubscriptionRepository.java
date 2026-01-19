package com.anime_registry.repository;

import com.anime_registry.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByUserId(Integer userId);
}