package com.mariekd.letsplay.authentication.services.implementations;

import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.jwt.JwtService;
import com.mariekd.letsplay.authentication.repositories.UserRepository;
import com.mariekd.letsplay.authentication.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String userName) { return userRepository.existsByName(userName); }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User updateUser(UUID id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }


    public String getJwtFromRequest(HttpServletRequest request) { //TODO : créer une classe pour déporter le code dupliqué de AdController
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    @Override
    public User getUserFromRequest (HttpServletRequest request) {
        SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        try {
            String userName = jwtService.getUserNameFromJwtToken(getJwtFromRequest(request));
            return userRepository.findByEmail(userName);
        } catch (JwtException e) {
            throw new AccessDeniedException("Invalid token"); //TODO : mieux gérer les try/catch
        } catch (Exception e) {
            throw new UsernameNotFoundException("Unknown user");
        }
    }

}
