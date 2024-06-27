package com.mariekd.letsplay.app.entities;

import com.mariekd.letsplay.authentication.entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "style")
@Data
public class Style {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false,  name="name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ads_styles",
            joinColumns = @JoinColumn(name = "style_id"),
            inverseJoinColumns = @JoinColumn(name = "ad_id"))
    private Set<Ad> ads = new HashSet<>();

    public Style(int id, String name, Set<Ad> ads) {
        this.id = id;
        this.name = name;
        this.ads = new HashSet<>();
        if (ads != null) {
            this.ads.addAll(ads);
            for (Ad ad : ads) {
                ad.getStyles().add(this);
            }
        }
    }

    public Style() {
    }

    public Style(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Ad> getAds() { return ads; }

    public void setAds(Set<Ad> ads) { this.ads = ads; }

    @Override
    public String toString() {
        return "Style{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Style)) return false;
        Style style = (Style) o;
        return getId() == style.getId() && getName().equals(style.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

