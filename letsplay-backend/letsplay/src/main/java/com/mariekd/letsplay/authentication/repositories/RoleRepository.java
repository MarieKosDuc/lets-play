package com.mariekd.letsplay.authentication.repositories;

import com.mariekd.letsplay.authentication.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository <Role, Long> {
}
