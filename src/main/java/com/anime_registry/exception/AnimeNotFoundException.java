package com.anime_registry.exception;

public class AnimeNotFoundException extends RuntimeException {
    public AnimeNotFoundException(String message) {
        super(message);
    }
}
