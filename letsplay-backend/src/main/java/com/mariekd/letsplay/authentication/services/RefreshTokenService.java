package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public interface RefreshTokenService {

    public RefreshToken createRefreshToken(String username);

    public Optional<RefreshToken> findByToken(String token);

    public RefreshToken verifyExpiration(RefreshToken token);

    @Transactional
    int deleteByUserId(UUID userId);
}
