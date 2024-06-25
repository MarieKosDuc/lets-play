package com.mariekd.letsplay.authentication.services.implementations;

import com.mariekd.letsplay.authentication.entities.ResetPasswordToken;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.repositories.ResetPasswordTokenRepository;
import com.mariekd.letsplay.authentication.repositories.UserRepository;
import com.mariekd.letsplay.authentication.services.ResetPasswordTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ResetPasswordTokenServiceImpl implements ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final UserRepository userRepository;

    public ResetPasswordTokenServiceImpl(ResetPasswordTokenRepository resetPasswordTokenRepository, UserRepository userRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResetPasswordToken createResetPasswordToken(User user) {
        ResetPasswordToken token = new ResetPasswordToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        return resetPasswordTokenRepository.save(token);
    }

    @Override
    public Optional<ResetPasswordToken> findByToken(String token) {
        return resetPasswordTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public int deleteByUserId(UUID userId) {
        return resetPasswordTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        resetPasswordTokenRepository.deleteByToken(token);
    }
}
