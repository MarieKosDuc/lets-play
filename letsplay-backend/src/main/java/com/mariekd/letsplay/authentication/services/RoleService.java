package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    Role findByName(String name);
}
