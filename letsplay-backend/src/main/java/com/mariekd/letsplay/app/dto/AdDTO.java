package com.mariekd.letsplay.app.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AdDTO {

    private int id;
    private Date createdAt;
    private String postedBy;
    private String title;
    private String seekingMusicianType;
    private String image;
    private String[] styles;
    private String location;
    private String description;

    public AdDTO(int id, Date createdAt, String postedBy, String title,
                 String seekingMusicianType, String image, String[] styles,
                 String location, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.postedBy = postedBy;
        this.title = title;
        this.seekingMusicianType = seekingMusicianType;
        this.image = image;
        this.styles = styles;
        this.location = location;
        this.description = description;
    }

    public AdDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeekingMusicianType() {
        return seekingMusicianType;
    }

    public void setSeekingMusicianType(String seekingMusicianType) {
        this.seekingMusicianType = seekingMusicianType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getStyles() {
        return styles;
    }

    public void setStyles(String[] styles) {
        this.styles = styles;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
