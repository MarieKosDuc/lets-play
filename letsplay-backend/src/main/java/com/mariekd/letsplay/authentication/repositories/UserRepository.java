package com.mariekd.letsplay.authentication.repositories;

import com.mariekd.letsplay.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName (String name);

    User findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM app_user WHERE user_id = :id", nativeQuery = true)
    void deleteById(@Param("id") UUID id);
}
