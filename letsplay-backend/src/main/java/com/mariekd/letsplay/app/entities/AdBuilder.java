package com.mariekd.letsplay.app.entities;

import com.mariekd.letsplay.authentication.entities.User;

import java.time.Instant;
import java.util.Set;

public class AdBuilder {
    private int id;
    private Instant createdAt;
    private User postedBy;
    private String title;
    private MusicianType from;
    private MusicianType searching;
    private String image;
    private Set<Style> styles;
    private Location location;
    private String description;
    private Set<User> likedByUsers;

    public AdBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public AdBuilder setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AdBuilder setPostedBy(User postedBy) {
        this.postedBy = postedBy;
        return this;
    }

    public AdBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public AdBuilder setFrom(MusicianType from) {
        this.from = from;
        return this;
    }

    public AdBuilder setSearching(MusicianType searching) {
        this.searching = searching;
        return this;
    }

    public AdBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public AdBuilder setStyles(Set<Style> styles) {
        this.styles = styles;
        return this;
    }

    public AdBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public AdBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public AdBuilder setLikedByUsers(Set<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
        return this;
    }

    public Ad build() {
        return new Ad(id, createdAt, postedBy, title, from, searching, image, styles, location, description, likedByUsers);
    }
}