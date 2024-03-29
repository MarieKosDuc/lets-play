package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Ad;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public interface AdService {
    List<Ad> getAllAds();

    Optional<Ad> getAdById(int id);

    Optional<Ad> getAdByTitle(String title);

    Ad createAd(Ad ad);

    Ad updateAd(int id, Ad ad);

    void deleteById(int id);
}
