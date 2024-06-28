package com.mariekd.letsplay.app.services.implementation;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.repositories.AdRepository;
import com.mariekd.letsplay.app.services.AdService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;

    public AdServiceImpl(AdRepository AdRepository) {
        this.adRepository = AdRepository;
    }

    @Override
    public List<Ad> getAllAds() {
        return adRepository.findAll();
    }

    @Override
    public List<Ad> getAllAdsSortedByReversedDate() {
        List<Ad> ads = adRepository.findAll();
        ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
        return ads;
    }

    @Override
    public Optional<Ad> getAdById(int id) {
        return adRepository.findById(id);
    }

    @Override
    public Optional<Ad> getAdByTitle(String title) {
        return adRepository.findByTitle(title);
    }

    @Override
    public Ad createAd(Ad ad) {
        return adRepository.save(ad);
    }

    @Override
    public Ad updateAd(int id, Ad ad) {
        if (adRepository.existsById(id)) {
            ad.setId(id);
            return adRepository.save(ad);
        }
        else {
            throw new EntityNotFoundException("Ad not found");
        }
    }

    @Override
    public void deleteById(int id) {
        adRepository.deleteById(id);
    }

    @Override
    public List<Ad> getAdsByUser(UUID userId) {
        return adRepository.findByUserId(userId);
    }

    @Override
    public List<Ad> getAdsByUserSortedByReversedDate(UUID userId) {
        List<Ad> ads = adRepository.findByUserId(userId);
        ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
        return ads;
    }

    @Override
    public List<Ad> getSearchedAds(String fromMusicianType, String searchedMusicianType, List<String> styles, String location) {
        List<Ad> ads = adRepository.findSearchedAds(fromMusicianType, searchedMusicianType, styles, location);
        ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
        return ads;
    }
}
