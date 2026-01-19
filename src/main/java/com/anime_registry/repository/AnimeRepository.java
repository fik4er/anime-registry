package com.anime_registry.repository;

import com.anime_registry.entity.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Integer> {

    List<Anime> findByIsBanned(Boolean isBanned);
    List<Anime> findByYear(Integer year);
    @Query("SELECT DISTINCT a FROM Anime a JOIN a.genres g WHERE g.name = :genreName")
    List<Anime> findByGenres_Name(@Param("genreName") String genreName);
    
    @Query("SELECT DISTINCT a FROM Anime a LEFT JOIN FETCH a.genres JOIN a.genres g WHERE g.name = :genreName")
    List<Anime> findByGenres_NameWithGenres(@Param("genreName") String genreName);
    
    List<Anime> findByAgeRating(String ageRating);
    
    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.genres WHERE a.titleRu LIKE %:searchTerm% OR a.titleEn LIKE %:searchTerm%")
    List<Anime> findByTitleRuContainingIgnoreCaseOrTitleEnContainingIgnoreCaseWithGenres(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.genres")
    List<Anime> findAllWithGenres();
    
    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.genres")
    Page<Anime> findAllWithGenres(Pageable pageable);
    
    Page<Anime> findAll(Pageable pageable);
    Page<Anime> findByYearAndAgeRatingAndIsBanned(Integer year, String ageRating, Boolean isBanned, Pageable pageable);

    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.genres WHERE a.id = :id")
    Optional<Anime> findByIdWithGenres(@Param("id") Integer id);

    @Query("SELECT COUNT(a) FROM Anime a JOIN a.genres g WHERE g.name = :genreName AND a.ageRating = :ageRating")
    long countByGenreAndAgeRating(@Param("genreName") String genreName, @Param("ageRating") String ageRating);

    @Query("SELECT COUNT(a) FROM Anime a JOIN a.genres g WHERE g.name = :genreName AND a.ageRating = :ageRating AND a.isBanned = true")
    long countByGenreAndAgeRatingAndIsBanned(@Param("genreName") String genreName, @Param("ageRating") String ageRating, @Param("isBanned") Boolean isBanned);
}

