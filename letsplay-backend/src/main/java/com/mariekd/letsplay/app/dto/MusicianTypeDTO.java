package com.mariekd.letsplay.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MusicianTypeDTO {
    private int id;
    private String name;

    public MusicianTypeDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MusicianTypeDTO() {
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