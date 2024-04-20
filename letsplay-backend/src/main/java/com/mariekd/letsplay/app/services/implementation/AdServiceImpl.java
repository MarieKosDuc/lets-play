package com.mariekd.letsplay.app.services.implementation;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.repositories.AdRepository;
import com.mariekd.letsplay.app.services.AdService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
            throw new UsernameNotFoundException("Ad not found");
        }
    }

    @Override
    public void deleteById(int id) {
        adRepository.deleteById(id);
    }

    @Override
    public List<Ad> getAdsByUser(String userName) {
        return adRepository.findByUserName(userName);
    }

    public List<Ad> getSearchedAds(String musicianType, List<String> styles, String location) {
        return adRepository.findSearchedAds(musicianType, styles, location);
    }
}
