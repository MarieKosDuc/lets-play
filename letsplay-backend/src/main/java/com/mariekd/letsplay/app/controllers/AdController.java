package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.services.MusicianTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mariekd.letsplay.app.dto.AdDTO;
import com.mariekd.letsplay.app.dto.mappers.AdMapper;
import com.mariekd.letsplay.app.entities.*;
import com.mariekd.letsplay.app.request.CreateAdRequest;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.app.services.LocationService;
import com.mariekd.letsplay.app.services.StyleService;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;

import java.util.*;

@RestController
@RequestMapping(value = "/api/ads", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(maxAge = 3600)
public class AdController {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AdController.class);
    private final AdService adService;
    private final UserService userService;
    private final MusicianTypeService musicianTypeService;
    private final StyleService styleService;
    private final LocationService locationService;

    public AdController(AdService adService, UserService userService, MusicianTypeService musicianTypeService,
                        StyleService styleService, LocationService locationService) {
        this.adService = adService;
        this.userService = userService;
        this.musicianTypeService = musicianTypeService;
        this.styleService = styleService;
        this.locationService = locationService;
    }

    @GetMapping("/get/all")
    public List<AdDTO> getAllAds() {
        List<Ad> ads = adService.getAllAdsSortedByReversedDate();
        return ads.stream().map(AdMapper::toAdDTO).toList();
    }

    @CrossOrigin(maxAge = 3600)
    @GetMapping("/search")
    public List<AdDTO> getSearchedAds(@RequestParam String from, @RequestParam String searching,
                                      @RequestParam List<String> styles, @RequestParam String location) {
        List<Ad> ads = adService.getSearchedAds(from, searching, styles, location);
        return ads.stream().map(AdMapper::toAdDTO).toList();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AdDTO> getAdById(@PathVariable("id") int id) {
        try {
            Optional<Ad> ad = adService.getAdById(id);
            return ad.map(value -> ResponseEntity.ok(AdMapper.toAdDTO(value))).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (final Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdDTO>> getAdsByUser(@PathVariable("userId") String userId) {
        UUID userUUID = UUID.fromString(userId);

        List<Ad> ads = adService.getAdsByUserSortedByReversedDate(userUUID);
        if (ads.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ads.stream().map(AdMapper::toAdDTO).toList());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<AdDTO> createAd(@RequestBody CreateAdRequest creatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        Date currentDate = new Date(System.currentTimeMillis());

        final MusicianType from = musicianTypeService.findByName(creatingAd.from());
        final MusicianType searching = musicianTypeService.findByName(creatingAd.searching());

        final Set<Style> adStyles = new HashSet<>();

        for (String styleName : creatingAd.styles()) {
            adStyles.add(styleService.findByName(styleName));
        }

        final Location adLocation = locationService.findByName(creatingAd.location());

        Ad ad = new AdBuilder()
                .setCreatedAt(currentDate.toInstant())
                .setPostedBy(postingUser)
                .setTitle(creatingAd.title())
                .setFrom(from)
                .setSearching(searching)
                .setStyles(adStyles)
                .setLocation(adLocation)
                .setDescription(creatingAd.description())
                .setImage(creatingAd.image())
                .setLikedByUsers(new HashSet<>())
                .build();

        try {
            Optional<Ad> existingAd = adService.getAdByTitle(creatingAd.title());
            if (existingAd.isPresent()) {
                logger.info("Error creating ad: ad with this title already exists");
                return ResponseEntity.badRequest().build();
            } else {
                try {
                    adService.createAd(ad);
                    logger.info("Ad {} created successfully", ad.getTitle());
                    AdDTO adDTO = AdMapper.toAdDTO(ad);
                    return ResponseEntity.ok(adDTO);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<AdDTO> updateAdByUser(@PathVariable("id") int id, @RequestBody CreateAdRequest updatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        Optional<Ad> ad = adService.getAdById(id);

        if (ad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!isUserAdAuthor(id, postingUser.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final MusicianType from = musicianTypeService.findByName(updatingAd.from());
        final MusicianType searching = musicianTypeService.findByName(updatingAd.searching());
        final Set<Style> adStyles = new HashSet<>();
        for (String styleName : updatingAd.styles()) {
            adStyles.add(styleService.findByName(styleName));
        }

        final Location adLocation = locationService.findByName(updatingAd.location());

        ad.get().setTitle(updatingAd.title());
        ad.get().setFrom(from);
        ad.get().setSearching(searching);
        ad.get().setStyles(adStyles);
        ad.get().setLocation(adLocation);
        ad.get().setDescription(updatingAd.description());
        ad.get().setImage(updatingAd.image());

        try {
            adService.updateAd(id, ad.get());
            logger.info("Ad {} updated successfully", ad.get().getTitle());
            AdDTO updatedAd = AdMapper.toAdDTO(ad.get());
            return ResponseEntity.ok((updatedAd));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAdByUser(@PathVariable("id") int id, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        if (adService.getAdById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!isUserAdAuthor(id, postingUser.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            adService.deleteById(id);
            logger.info("Ad {} deleted successfully", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/favorites/{id}")
    public ResponseEntity<List<AdDTO>> getFavoriteAds(@PathVariable("id") UUID id) {
        try {
            User user = userService.getUserById(id);
            List<Ad> likedAds = user.getLikedAdsList();

            if (likedAds.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            likedAds.sort(Comparator.comparing(Ad::getCreatedAt).reversed());

            return ResponseEntity.ok(likedAds.stream().map(AdMapper::toAdDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favorites/{userId}/{adId}")
    public ResponseEntity<Void> toggleFavoriteAd(@PathVariable("userId") UUID userId, @PathVariable("adId") int adId) {
        try {
            User user = userService.getUserById(userId);
            Optional<Ad> adOptional = adService.getAdById(adId);

            if (adOptional.isPresent()) {
                Ad ad = adOptional.get();
                if (user.getLikedAds().contains(ad)) {
                    user.removeLikedAd(ad);
                } else {
                    user.addLikedAd(ad);
                }
                userService.updateUser(user.getId(), user);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public boolean isUserAdAuthor(int adId, String currentUserName) {
        Optional<Ad> ad = adService.getAdById(adId);
        return ad.map(value -> value.getPostedBy().getName().equals(currentUserName)).orElse(false);
    }
}
