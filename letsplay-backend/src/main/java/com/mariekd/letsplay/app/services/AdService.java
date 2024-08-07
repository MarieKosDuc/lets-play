package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Ad;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface AdService {
    List<Ad> getAllAds();

    List<Ad> getAllAdsSortedByReversedDate();

    Optional<Ad> getAdById(int id);

    Optional<Ad> getAdByTitle(String title);

    Ad createAd(Ad ad);

    Ad updateAd(int id, Ad ad);

    void deleteById(int id);

    List<Ad> getAdsByUser(UUID userId);

    List<Ad> getAdsByUserSortedByReversedDate(UUID userId);

    List<Ad> getSearchedAds(String fromMusicianType, String searchedMusicianType, List<String> styles, String location);
}
