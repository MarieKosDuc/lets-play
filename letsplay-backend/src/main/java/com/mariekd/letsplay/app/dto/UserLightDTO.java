package com.mariekd.letsplay.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLightDTO {
    private UUID id;
    private String name;

    public UserLightDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserLightDTO() {
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
}