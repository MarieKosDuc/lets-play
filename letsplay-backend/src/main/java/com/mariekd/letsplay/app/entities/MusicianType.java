package com.mariekd.letsplay.app.entities;

import jakarta.persistence.*;

import lombok.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "musician_type")
@Data
public class MusicianType {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false,  name="name")
    private String name;

    @OneToMany(mappedBy = "seekingMusicianType", fetch = FetchType.LAZY)
    private Set<Ad> ads;

    public MusicianType(int id, String name, Set<Ad> ads) {
        this.id = id;
        this.name = name;
        this.ads = ads;
    }

    public MusicianType() {
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
        return "MusicianType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicianType)) return false;
        MusicianType musicianType = (MusicianType) o;
        return getId() == musicianType.getId() && getName().equals(musicianType.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

