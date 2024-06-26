package com.mariekd.letsplay.app.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class AdDTO {

    private int id;
    private Instant createdAt;
    private String postedByName;
    private String postedById;
    private String title;
    private String from;
    private String searching;
    private String image;
    private String[] styles;
    private String location;
    private String description;

    public AdDTO(int id, Instant createdAt, String postedBy, String title, String from, String searching, String image, String[] styles, String location, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.postedByName = postedBy;
        this.title = title;
        this.from = from;
        this.searching = searching;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public String getPostedById() { return postedById; }

    public void setPostedById(String postedById) { this.postedById = postedById; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }

    public String getSearching() { return searching; }

    public void setSearching(String searching) { this.searching = searching; }

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
