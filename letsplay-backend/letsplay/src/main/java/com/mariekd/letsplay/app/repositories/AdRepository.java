package com.mariekd.letsplay.app.repositories;

import com.mariekd.letsplay.app.entities.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository <Ad, Integer> {

    @Modifying
    @Query("DELETE FROM Ad a WHERE a.id = :id")
    void deleteById(int id);

    Optional<Ad> findByTitle (String title);

    //@Query("SELECT a FROM Ad a WHERE a.seekingMusicianType = :musicianType AND a.location = :location AND a.styles IN :styles")
    @Query(value = "SELECT DISTINCT ON (a.id) a.id, a.created_at, a.posted_by, a.title, a.seeking_musician_type , a.image, a.\"location\" , a.description " +
            "FROM music.ad a LEFT JOIN music.ads_styles ads ON a.id = ads.ad_id LEFT JOIN music.style s on ads.style_id = s.id " +
            "LEFT JOIN music.musician_type mt ON a.seeking_musician_type = mt.id " +
            "LEFT JOIN music.location l ON a.location = l.id " +
            "WHERE mt.name = :musicianType AND l.name = :location " +
            "AND s.name in (:styles)", nativeQuery = true)
    List<Ad> findSearchedAds(String musicianType, List<String> styles, String location);

}
