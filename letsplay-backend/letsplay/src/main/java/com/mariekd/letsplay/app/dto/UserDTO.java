package com.mariekd.letsplay.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
}