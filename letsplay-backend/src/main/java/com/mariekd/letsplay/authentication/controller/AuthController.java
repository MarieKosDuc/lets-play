package com.mariekd.letsplay.authentication.controller;

import com.mariekd.letsplay.authentication.entities.RefreshToken;
import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.payload.request.SignupRequest;
import com.mariekd.letsplay.authentication.payload.request.TokenRefreshRequest;
import com.mariekd.letsplay.authentication.payload.response.LoginOkResponse;
import com.mariekd.letsplay.authentication.payload.response.TokenRefreshResponse;
import com.mariekd.letsplay.authentication.payload.response.UserInfoResponse;
import com.mariekd.letsplay.authentication.jwt.JwtService;
import com.mariekd.letsplay.authentication.payload.request.LoginRequest;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.models.UserInfo;
import com.mariekd.letsplay.authentication.services.RefreshTokenService;
import com.mariekd.letsplay.authentication.services.implementations.RoleServiceImpl;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(maxAge = 3600)
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService, UserServiceImpl userService, RoleServiceImpl roleService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.roleService = roleService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOkResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserInfo userDetails = (UserInfo) authentication.getPrincipal();

        String jwt = jwtService.generateJwtToken(userDetails.getUsername());

        final List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User connecterUser = userService.getUserByEmail(userDetails.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());


        LOGGER.info("User {} / {} logged in", userDetails.getUsername(), connecterUser.getName());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwt)
                        .body(new LoginOkResponse(refreshToken.getToken(), connecterUser.getId(), connecterUser.getName(),
                                connecterUser.getProfilePicture(), connecterUser.getEmail(), roles));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newJwt = jwtService.generateJwtToken(user.getEmail());
                    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newJwt)
                            .body(new TokenRefreshResponse(requestRefreshToken));
                })
                .orElseThrow(() -> new AccessDeniedException("Invalid refresh token"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        ResponseCookie cookie = jwtService.getCleanJwtCookie();

        User connectedUser = userService.getUserFromRequest(request);
        refreshTokenService.deleteByUserId(connectedUser.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<User> getAllUsers() {
        LOGGER.info("Getting all users: {} ", userService.getAllUsers());
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody SignupRequest userInfos) {
        try {
            if (!userService.existsByEmail(userInfos.email()) && !userService.existsByUserName(userInfos.username())) {
                Role userRole = roleService.findByName("USER");

                User user = new User(UUID.randomUUID() , userInfos.username(), userInfos.email(), userInfos.password(), userRole, userInfos.profilePicture(),null);

                userService.createUser(user);
                LOGGER.info("User created: {}", user.getName());

                return ResponseEntity.ok().body("User created successfully: " + user.getName());
            } else {
                LOGGER.error("User already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this name or email already exists");
            }
        } catch (final Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable UUID id, @RequestBody User user, HttpServletRequest request) {

        if (isUserAuthorizedToModify(id, request)) {
            return userService.updateUser(id, user);
        } else {
            throw new AccessDeniedException("You are not authorized to modify this user");
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id, HttpServletRequest request){

        if (isUserAuthorizedToModify(id, request)) {
            userService.deleteUser(id);
        } else {
            throw new AccessDeniedException("You are not authorized to delete this user");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public void deleteUserByAdmin(@PathVariable UUID id){
            userService.deleteUser(id);
    }


    public boolean isUserAuthorizedToModify(UUID userId, HttpServletRequest request) {
        User connectedUser = userService.getUserFromRequest(request);

        if (connectedUser.getId().equals(userId)) {
            return true;
        } else {
            throw new AccessDeniedException("You are not authorized to modify this user");
        }
    }

}
