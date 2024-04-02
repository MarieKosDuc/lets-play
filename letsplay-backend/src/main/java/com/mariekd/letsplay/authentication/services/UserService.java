package com.mariekd.letsplay.authentication.services;

import com.mariekd.letsplay.authentication.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    List<User> getAllUsers();
    User getUserById(UUID id);
    User getUserByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    User createUser(User user);
    User updateUser(UUID id, User user);
    void deleteUser(UUID id);

    User getUserFromRequest (HttpServletRequest request);
}
