package com.mariekd.letsplay.authentication.entities;

import com.mariekd.letsplay.app.entities.Ad;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "email" }) )
public class User {

    @Id
    @Column(updatable = false, nullable = false, name="user_id")
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false, name="name")
    private String name;

    @Column(length = 50, nullable = false, name="email")
    private String email;

    @Column(length = 255, nullable = false, name="password")
    private String password;

    @Column(length = 255, nullable = true, name="profile_picture")
    private String profilePicture;

    @Column(name = "valid")
    private boolean valid;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "postedBy", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ad> ads;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_liked_ads",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ad_id"))
    private Set<Ad> likedAds;

    public User(UUID id, String name, String email, String password, String profilePicture,
                Boolean valid, Role role, Set<Ad> ads, Set<Ad> likedAds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.valid = valid;
        this.ads = ads;
        this.roles = new HashSet<>();
        if (role != null) {
            this.roles.add(role);
            role.getUsers().add(this);
        }
        this.likedAds = likedAds != null ? likedAds : new HashSet<>();
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isValid() { return valid; }

    public void setValid(boolean valid) { this.valid = valid; }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Ad> getAds() {
        return ads;
    }

    public void setAds(Set<Ad> ads) {
        this.ads = ads;
    }

    public Set<Ad> getLikedAds() { return likedAds; }

    public void setLikedAds(Set<Ad> likedAds) { this.likedAds = likedAds; }

    // Methods to add or remove liked ads
    public void addLikedAd(Ad ad) {
        this.likedAds.add(ad);
        ad.getLikedByUsers().add(this);
    }

    public void removeLikedAd(Ad ad) {
        this.likedAds.remove(ad);
        ad.getLikedByUsers().remove(this);
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }

}
