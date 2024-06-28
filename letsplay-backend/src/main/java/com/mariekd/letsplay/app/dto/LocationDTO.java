package com.mariekd.letsplay.app.dto;


public class LocationDTO {
    private int id;
    private String name;

    public LocationDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public LocationDTO() {
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
}