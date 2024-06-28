package com.mariekd.letsplay.authentication.services.implementations;

import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.entities.ValidAccountToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.mariekd.letsplay.authentication.repositories.ValidAccountTokenRepository;
import com.mariekd.letsplay.authentication.repositories.UserRepository;
import com.mariekd.letsplay.authentication.services.ValidAccountTokenService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValidAccountTokenServiceImpl implements ValidAccountTokenService {

    private final ValidAccountTokenRepository validAccountTokenRepository;
    private final UserRepository userRepository;

    public ValidAccountTokenServiceImpl(ValidAccountTokenRepository validAccountTokenRepository, UserRepository userRepository) {
        this.validAccountTokenRepository = validAccountTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ValidAccountToken createValidationToken(User user) {
        ValidAccountToken token = new ValidAccountToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(24 *12 * 60 * 1000)); //sets expiry to 24 hours

        return validAccountTokenRepository.save(token);
    }

    @Override
    public Optional<ValidAccountToken> findByToken(String token) {
        return validAccountTokenRepository.findByToken(token);
    }

    @Override
    public ValidAccountToken verifyExpiration(ValidAccountToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            validAccountTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please login again!");
        }
        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(UUID userId) {
        return validAccountTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        validAccountTokenRepository.deleteByToken(token);
    }
}
