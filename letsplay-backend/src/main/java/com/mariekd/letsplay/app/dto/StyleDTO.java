package com.mariekd.letsplay.app.dto;

public class StyleDTO {
    private int id;
    private String name;

    public StyleDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public StyleDTO() {
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