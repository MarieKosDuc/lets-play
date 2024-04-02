package com.mariekd.letsplay.authentication.services.implementations;

import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.repositories.RoleRepository;
import com.mariekd.letsplay.authentication.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
