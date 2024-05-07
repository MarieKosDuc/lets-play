package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.entities.ValidAccountToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public interface ValidAccountTokenService {

    ValidAccountToken createValidationToken(User user);

    Optional<ValidAccountToken> findByToken(String token);

    ValidAccountToken verifyExpiration(ValidAccountToken token);

    @Transactional
    int deleteByUserId(UUID userId);

    @Transactional
    void deleteByToken(String token);
}
