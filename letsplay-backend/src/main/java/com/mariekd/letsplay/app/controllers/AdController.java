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
import com.mariekd.letsplay.authentication.exceptions.UnauthorizedException;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import jakarta.persistence.JoinTable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;

@RestController
@RequestMapping(value="/api/ads", produces = MediaType.APPLICATION_JSON_VALUE)
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
        try {
            List<Ad> ads = adService.getAllAds();
            ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
            return ads.stream().map(AdMapper::toAdDTO).toList();
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting all ads");
        }

    }

    @CrossOrigin(maxAge = 3600)
    @GetMapping("/search")
    public List<AdDTO> getSearchedAds(@RequestParam String from, @RequestParam String searching, @RequestParam List<String> styles,
                                      @RequestParam String location) {
        try {
            List<Ad> ads = adService.getSearchedAds(from, searching, styles, location);
            ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
            return ads.stream().map(AdMapper::toAdDTO).toList();
        } catch (final Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting searched ads");
        }

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AdDTO> getAdById(@PathVariable("id") int id) {
        try {
            Optional<Ad> ad = adService.getAdById(id);
            return ad.map(value -> ResponseEntity.ok(AdMapper.toAdDTO(value)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (final Exception e){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting ad by id");
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdDTO>> getAdsByUser(@PathVariable("userId") String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);

            List<Ad> ads = adService.getAdsByUser(userUUID);
            if (ads.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                ads.sort(Comparator.comparing(Ad::getCreatedAt).reversed());
                return ResponseEntity.ok(ads.stream().map(AdMapper::toAdDTO).toList());
            }
        } catch (final Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting ads by user");
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Object> createAd(@RequestBody CreateAdRequest creatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        Date currentDate = new Date(System.currentTimeMillis());

        final MusicianType from = musicianTypeService.findByName(creatingAd.from());
        final MusicianType searching = musicianTypeService.findByName(creatingAd.searching());

        final Set<Style> adStyles = new HashSet<>();
        for (String styleName : creatingAd.styles()) {
            adStyles.add(styleService.findByName(styleName));
        }

        final Location adLocation = locationService.findByName(creatingAd.location());

        Ad ad = new Ad();
        ad.setCreatedAt(currentDate.toInstant());
        ad.setPostedBy(postingUser);
        ad.setTitle(creatingAd.title());
        ad.setFrom(from);
        ad.setSearching(searching);
        ad.setStyles(adStyles);
        ad.setLocation(adLocation);
        ad.setDescription(creatingAd.description());
        ad.setImage(creatingAd.image());
        ad.setLikedByUsers(new HashSet<>());

        try {
            Optional<Ad> existingAd = adService.getAdByTitle(creatingAd.title());
            if (existingAd.isPresent()) {
                return setErrorResponse("Ad with this title already exists", HttpStatus.BAD_REQUEST);
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
            return setErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAdByUser(@PathVariable("id") int id, @RequestBody CreateAdRequest updatingAd, HttpServletRequest request) {

        User postingUser = userService.getUserFromRequest(request);

        try {
            if (!isUserAdAuthor(id, postingUser.getName())) { //TODO : écrire test pour valider qu'un user ne peut pas modifier l'annonce d'un autre user
                return setErrorResponse("You can only update your own ads", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return setErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<Ad> ad = adService.getAdById(id);

        if (ad.isPresent()) {
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
                AdDTO updatedAd = AdMapper.toAdDTO(ad.get());
                return ResponseEntity.ok((updatedAd));}
            catch (Exception e) {
                return setErrorResponse("Error updating ad", HttpStatus.BAD_REQUEST);
            }
        } else {
            return setErrorResponse("Ad not found", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteAdByUser(@PathVariable("id") int id, HttpServletRequest request) throws Exception {

        User postingUser = userService.getUserFromRequest(request);

        if (!isUserAdAuthor(id, postingUser.getName())) {
            throw new UnauthorizedException("You can only delete your own ads");
        }

        try {
            adService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting ad");
        }
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/favorites/{id}")
    public ResponseEntity<List<AdDTO>> getFavorites(@PathVariable("id") UUID id) {
        try {
            User user = userService.getUserById(id);
            Set<Ad> likedAds = user.getLikedAds();

            if(likedAds.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Ad> likedAdsList = new ArrayList<>(likedAds);

            likedAdsList.sort(Comparator.comparing(Ad::getCreatedAt).reversed());

            return ResponseEntity.ok(likedAdsList.stream().map(AdMapper::toAdDTO).toList());
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting favorite ads");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favorites/{userId}/{adId}")
    public ResponseEntity<Object> toggleFavoriteAd(@PathVariable("userId") UUID userId, @PathVariable("adId") int adId) {
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
                return setErrorResponse("Ad not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error toggling favorite ad");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Object> deleteAdByAdmin(@PathVariable("id") int id) {

        try {
            adService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (final Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting ad by admin");
        }
    }

    public boolean isUserAdAuthor(int adId, String currentUserName) throws Exception {
        Optional<Ad> ad = adService.getAdById(adId);
        if (ad.isPresent()) {
            return ad.get().getPostedBy().getName().equals(currentUserName);
        } else {
            throw new Exception("Ad not found");
        }
    }

    public ResponseEntity<Object> setErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
