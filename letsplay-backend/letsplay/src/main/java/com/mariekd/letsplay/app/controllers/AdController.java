package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.dto.AdDTO;
import com.mariekd.letsplay.app.dto.mappers.AdMapper;
import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.entities.Location;
import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.entities.Style;
import com.mariekd.letsplay.app.request.CreateAdRequest;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.app.services.implementation.LocationServiceImpl;
import com.mariekd.letsplay.app.services.implementation.MusicianTypeServiceImpl;
import com.mariekd.letsplay.app.services.implementation.StyleServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin(maxAge = 3600)
public class AdController {

    private final AdServiceImpl adService;
    private final UserServiceImpl userService;
    private final MusicianTypeServiceImpl musicianTypeService;
    private final StyleServiceImpl styleService;
    private final LocationServiceImpl locationService;


    public AdController(AdServiceImpl adService, UserServiceImpl userService,
                        MusicianTypeServiceImpl musicianTypeService, StyleServiceImpl styleService,
                        LocationServiceImpl locationService) {
        this.adService = adService;
        this.userService = userService;
        this.musicianTypeService = musicianTypeService;
        this.styleService = styleService;
        this.locationService = locationService;
    }

    @GetMapping("/get/all")
    public List<AdDTO> getAllAds() {
        List<Ad> ads = adService.getAllAds();
        ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
        return ads.stream().map(AdMapper::toAdDTO).toList();
    }

    @GetMapping("/search")
    public List<AdDTO> getSearchedAds(@RequestParam String musicianType, @RequestParam List<String> styles, @RequestParam String location) {
        List<Ad> ads = adService.getSearchedAds(musicianType, styles, location);
        ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
        return ads.stream().map(AdMapper::toAdDTO).toList();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAdById(@PathVariable int id) {
        Optional<Ad> ad = adService.getAdById(id);
        if (ad.isPresent()){
            return ResponseEntity.ok(AdMapper.toAdDTO(ad.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createAd(@RequestBody CreateAdRequest creatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        Date currentDate = new Date(System.currentTimeMillis());

        final MusicianType adMusicianType = musicianTypeService.findByName(creatingAd.musicianType());
        final Set<Style> adStyles = new HashSet<>();
        for (String styleName : creatingAd.styles()) {
            adStyles.add(styleService.findByName(styleName));
        }

        final Location adLocation = locationService.findByName(creatingAd.location());

        Ad ad = new Ad();
        ad.setCreatedAt(Date.from(currentDate.toInstant()));
        ad.setPostedBy(postingUser);
        ad.setTitle(creatingAd.title());
        ad.setSeekingMusicianType(adMusicianType);
        ad.setStyles(adStyles);
        ad.setLocation(adLocation);
        ad.setDescription(creatingAd.description());
        ad.setImage(creatingAd.image());


        try {
            Optional<Ad> existingAd = adService.getAdByTitle(creatingAd.title());
            if (existingAd.isPresent()) {
                return ResponseEntity.badRequest().body("Ad with this title already exists");
            } else {
                try {
                    adService.createAd(ad);
                    AdDTO adDTO = AdMapper.toAdDTO(ad);
                    return ResponseEntity.ok(adDTO);
                } catch (Exception e) {

                    return ResponseEntity.badRequest().body(e.getMessage());
                }

            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating ad");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdByUser(@PathVariable int id, @RequestBody CreateAdRequest updatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        try {
            if (!isUserAdAuthor(id, postingUser.getName())) { //TODO : écrire test pour valider qu'un user ne peut pas modifier l'annonce d'un autre user
                return ResponseEntity.badRequest().body("You can only update your own ads");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        Optional<Ad> ad = adService.getAdById(id);

        if (ad.isPresent()) {
            final MusicianType adMusicianType = musicianTypeService.findByName(updatingAd.musicianType());
            final Set<Style> adStyles = new HashSet<>();
            for (String styleName : updatingAd.styles()) {
                adStyles.add(styleService.findByName(styleName));
            }
            final Location adLocation = locationService.findByName(updatingAd.location());

            ad.get().setTitle(updatingAd.title()); //TODO : créer un ad builder
            ad.get().setSeekingMusicianType(adMusicianType);
            ad.get().setStyles(adStyles);
            ad.get().setLocation(adLocation);
            ad.get().setDescription(updatingAd.description());
            ad.get().setImage(updatingAd.image());

            try {
                adService.updateAd(id, ad.get());
                return ResponseEntity.ok().body("Ad updated");}
            catch (Exception e) {
                return ResponseEntity.badRequest().body("Error updating ad");
            }
        } else {
            return ResponseEntity.badRequest().body("Ad not found");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdByUser(@PathVariable int id, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        try {
            if (!isUserAdAuthor(id, postingUser.getName())) {
                return ResponseEntity.badRequest().body("You can only delete your own ads");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        try {
            adService.deleteById(id);
            return ResponseEntity.ok().body("Ad deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting ad");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/admin/{id}")
    public ResponseEntity<?> deleteAdByAdmin(@PathVariable int id) {

        try {
            adService.deleteById(id);
            return ResponseEntity.ok().body("Ad deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting ad");
        }
    }

    public boolean isUserAdAuthor(int adId, String currentUserName) throws Exception {
        Optional<Ad> ad = adService.getAdById(adId);
        if (ad.isPresent()) {
            return ad.get().getPostedBy().getName().equals(currentUserName);
        } else {
            throw new Exception("Ad not found"); //TODO : créer exception spécifique
        }
    }
}
