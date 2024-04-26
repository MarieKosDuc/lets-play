package com.mariekd.letsplay.authentication.controller;

import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.authentication.entities.RefreshToken;
import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.entities.ValidAccountToken;
import com.mariekd.letsplay.authentication.payload.request.SignupRequest;
import com.mariekd.letsplay.authentication.payload.request.TokenRefreshRequest;
import com.mariekd.letsplay.authentication.payload.response.LoginOkResponse;
import com.mariekd.letsplay.authentication.payload.response.TokenRefreshResponse;
import com.mariekd.letsplay.authentication.jwt.JwtService;
import com.mariekd.letsplay.authentication.payload.request.LoginRequest;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.models.UserInfo;
import com.mariekd.letsplay.authentication.services.RefreshTokenService;
import com.mariekd.letsplay.authentication.services.ValidAccountTokenService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private final ValidAccountTokenService validAccountTokenService;

    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService, UserServiceImpl userService, RoleServiceImpl roleService,
                          RefreshTokenService refreshTokenService, ValidAccountTokenService validAccountTokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.roleService = roleService;
        this.refreshTokenService = refreshTokenService;
        this.validAccountTokenService = validAccountTokenService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOkResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
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

            if (!connecterUser.isValid()) {
                throw new AccessDeniedException("User is not valid");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());


            LOGGER.info("User {} / {} logged in", userDetails.getUsername(), connecterUser.getName());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwt)
                    .body(new LoginOkResponse(refreshToken.getToken(), connecterUser.getId(), connecterUser.getName(),
                            connecterUser.getProfilePicture(), connecterUser.getEmail(), roles));
        } catch (AccessDeniedException e) {
            LOGGER.error("Error authenticating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        LOGGER.info("Refreshing token: {} ", request.getToken());
        String requestRefreshToken = request.getToken();

        try {
            String jwt = jwtService.getJwtFromCookies(httpRequest);
            User jwtUser = userService.getUserByEmail(jwtService.getUserNameFromJwtToken(jwt));

            Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(requestRefreshToken);
            if (optionalRefreshToken.isPresent()) {

                refreshTokenService.verifyExpiration(optionalRefreshToken.get());

                User refreshTokenUser = optionalRefreshToken.get().getUser();

                if (!jwtUser.getId().equals(refreshTokenUser.getId())) {
                    throw new AccessDeniedException("Invalid refresh token");
                }

                String newJwt = jwtService.generateJwtToken(refreshTokenUser.getEmail());

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newJwt)
                        .body(new TokenRefreshResponse(requestRefreshToken));

            } else {
                throw new AccessDeniedException("Invalid refresh token");
            }
        } catch (AccessDeniedException e) {
            LOGGER.error("Invalid refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
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


    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody SignupRequest userInfos) {
        try {
            if (!userService.existsByEmail(userInfos.email()) && !userService.existsByUserName(userInfos.username())) {
                Role userRole = roleService.findByName("USER");

                User user = new User(UUID.randomUUID() , userInfos.username(), userInfos.email(), userInfos.password(), userInfos.profilePicture(),
                        false, userRole, null);

                userService.createUser(user);
                LOGGER.info("User created: {}", user.getName());

                ValidAccountToken validationToken = validAccountTokenService.createValidationToken(user);

                emailService.sendHtmlEmail(user.getEmail(), "Validation de ton compte Let's Play",
                        "Bienvenue sur Let's Play !", "Pour valider ton compte, clique sur le lien suivant :" +
                                "\n Attention, ce lien est valable 24h ! \n",
                        "http://localhost:8080/api/users/verify/" + validationToken.getToken());

                LOGGER.info("Validation email sent to: {}", user.getEmail(), " with token: {}", validationToken.getToken());


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

    @PostMapping("/verify/{token}")
    public ResponseEntity<String> validateUser(@PathVariable("token") String token) {
        try {
            ValidAccountToken checkedToken = validAccountTokenService.findByToken(token) // Ne fonctionne pas
                    .orElseThrow(() -> new RuntimeException("Token not found"));

            User user = checkedToken.getUser();

            user.setValid(true);
            userService.updateUser(user.getId(), user);

            validAccountTokenService.deleteByToken(token);

            LOGGER.info("User validated: {}", user.getName());

            return ResponseEntity.ok().body("User validated successfully: " + user.getName());
        } catch (final Exception e) {
            LOGGER.error("Error validating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error validating user: " + e.getMessage()); //TODO changer type d'erreur
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<User> getAllUsers() {
        LOGGER.info("Getting all users: {} ", userService.getAllUsers());
        return userService.getAllUsers();
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
