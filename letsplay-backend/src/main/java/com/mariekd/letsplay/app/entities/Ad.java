package com.mariekd.letsplay.app.entities;

import com.mariekd.letsplay.authentication.entities.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

import lombok.*;

@Entity
@Table(name = "ad")
@Data
public class Ad {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "posted_by", nullable = false, referencedColumnName = "user_id")
    private User postedBy;

    @Column(nullable = false, name="title")
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="from_musician", nullable = false, referencedColumnName = "id")
    private MusicianType from;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="searching_musician", nullable = false, referencedColumnName = "id")
    private MusicianType searching;

    @Column(nullable = false, name="image")
    private String image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ads_styles",
            joinColumns = @JoinColumn(name = "ad_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id"))
    private Set<Style> styles = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="location", nullable = false, referencedColumnName = "id")
    private Location location;

    @Column(nullable = false, name="description")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_liked_ads",
            joinColumns = @JoinColumn(name = "ad_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedByUsers = new HashSet<>();

    public Ad (int id, Instant createdAt, User postedBy, String title, MusicianType searching, String image,
               Set<Style> styles, Location location, String description, Set<User> likedByUsers) {
        this.id = id;
        this.createdAt = createdAt;
        this.postedBy = postedBy;
        this.title = title;
        this.searching = searching;
        this.image = image;
        this.styles = styles;
        this.location = location;
        this.description = description;
        this.likedByUsers = likedByUsers != null ? likedByUsers : new HashSet<>();
    }

    public Ad() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MusicianType getFrom() {
        return from;
    }

    public void setFrom(MusicianType from) {
        this.from = from;
    }

    public MusicianType getSearching() {
        return searching;
    }

    public void setSearching(MusicianType searching) {
        this.searching = searching;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Style> getStyles() {
        return styles;
    }

    public void setStyles(Set<Style> styles) {
        this.styles = styles;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    // Methods to add or remove users who liked this ad
    public void addLikedByUser(User user) {
        this.likedByUsers.add(user);
        user.getLikedAds().add(this);
    }

    public void removeLikedByUser(User user) {
        this.likedByUsers.remove(user);
        user.getLikedAds().remove(this);
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", postedBy=" + postedBy +
                ", title='" + title + '\'' +
                ", from=" + from +
                ", searching for=" + searching +
                ", image='" + image + '\'' +
                ", style=" + styles +
                ", location=" + location +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return id == ad.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
