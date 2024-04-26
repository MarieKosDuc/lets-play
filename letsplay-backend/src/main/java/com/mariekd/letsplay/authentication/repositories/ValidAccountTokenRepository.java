package com.mariekd.letsplay.authentication.repositories;

import com.mariekd.letsplay.authentication.entities.ValidAccountToken;
import com.mariekd.letsplay.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidAccountTokenRepository extends JpaRepository<ValidAccountToken, Integer> {
    Optional<ValidAccountToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);

    @Modifying
    void deleteByToken(String token);
}
