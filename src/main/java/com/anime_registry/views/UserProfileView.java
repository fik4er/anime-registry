package com.anime_registry.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileView {
    private Integer id;
    private String email;
    private List<ShowAnimeDto> favorites;
    private boolean isSubscribed;
    private String subscriptionScope;
}
