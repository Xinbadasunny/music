package com.vocalcoach.infrastructure.song.repository;

import com.vocalcoach.infrastructure.song.dataobject.SongDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<SongDO, Long> {

    List<SongDO> findByCategory(String category);

    @Query("SELECT s FROM SongDO s WHERE s.name LIKE %:keyword% OR s.artist LIKE %:keyword%")
    List<SongDO> search(@Param("keyword") String keyword);
}
