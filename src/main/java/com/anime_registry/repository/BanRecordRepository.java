package com.anime_registry.repository;

import com.anime_registry.entity.BanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BanRecordRepository extends JpaRepository<BanRecord, Integer> {
    List<BanRecord> findByAnime_Id(Integer animeId);
}