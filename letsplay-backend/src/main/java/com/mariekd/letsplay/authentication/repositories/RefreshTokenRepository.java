package com.mariekd.letsplay.authentication.repositories;

import com.mariekd.letsplay.authentication.entities.RefreshToken;
import com.mariekd.letsplay.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);

    @Modifying
    void deleteByToken(String token);
}
