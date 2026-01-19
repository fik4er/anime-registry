package com.anime_registry;

import com.anime_registry.config.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RedisConfig.class)
public class AnimeRegistryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimeRegistryApiApplication.class, args);
	}

}
