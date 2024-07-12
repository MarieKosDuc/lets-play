package com.mariekd.letsplay.app.repositories;

import com.mariekd.letsplay.app.entities.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdRepository extends JpaRepository <Ad, Integer> {

    @Modifying
    @Query("DELETE FROM Ad a WHERE a.id = :id")
    void deleteById(@Param("id") int id);

    Optional<Ad> findByTitle (String title);

    @Query ("SELECT a FROM Ad a WHERE a.postedBy.id = :userId")
    List<Ad> findByUserId (UUID userId);

    @Query("SELECT a FROM Ad a WHERE a.postedBy.name = :userName")
    List<Ad> findByUserName(String userName);

    @Query(value = "SELECT DISTINCT ON (a.id) a.id, a.created_at, a.user_id, a.title, a.from_musician, a.searching_musician, a.image, a.\"location\" , a.description " +
            "FROM music.ad a LEFT JOIN music.ad_style ads ON a.id = ads.ad_id LEFT JOIN music.style s on ads.style_id = s.id " +
            "LEFT JOIN music.musician_type mt1 ON a.from_musician = mt1.id " +
            "LEFT JOIN music.musician_type mt2 ON a.searching_musician = mt2.id " +
            "LEFT JOIN music.location l ON a.location = l.id " +
            "WHERE mt1.name = :fromMusicianType AND mt2.name = :searchedMusicianType " +
            "AND s.name in (:styles) AND l.name = :location", nativeQuery = true)
    List<Ad> findSearchedAds(@Param("fromMusicianType") String fromMusicianType, @Param("searchedMusicianType") String searchedMusicianType,
                             @Param("styles") List<String> styles, @Param("location") String location);

}
