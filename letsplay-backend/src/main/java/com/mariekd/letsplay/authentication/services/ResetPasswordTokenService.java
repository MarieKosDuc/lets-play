package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.ResetPasswordToken;
import com.mariekd.letsplay.authentication.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public interface ResetPasswordTokenService {

    ResetPasswordToken createResetPasswordToken(User user);

    Optional<ResetPasswordToken> findByToken(String token);

    @Transactional
    int deleteByUserId(UUID userId);

    @Transactional
    void deleteByToken(String token);
}
