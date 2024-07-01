package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public interface RefreshTokenService {

    RefreshToken createRefreshToken(String username);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    @Transactional
    void deleteByUserId(UUID userId);

    @Transactional
    void deleteByToken(String token);
}
