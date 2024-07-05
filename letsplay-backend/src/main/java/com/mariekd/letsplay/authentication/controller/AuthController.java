package com.mariekd.letsplay.authentication.controller;

import com.mariekd.letsplay.authentication.dto.UserDTO;
import com.mariekd.letsplay.authentication.entities.*;
import com.mariekd.letsplay.authentication.payload.request.*;
import com.mariekd.letsplay.authentication.payload.response.UserInfoResponse;
import com.mariekd.letsplay.authentication.services.*;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.authentication.payload.response.LoginOkResponse;
import com.mariekd.letsplay.authentication.payload.response.TokenRefreshResponse;
import com.mariekd.letsplay.authentication.jwt.JwtService;
import com.mariekd.letsplay.authentication.models.UserInfo;

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
    private final UserService userService;
    private final RoleService roleService;
    private final RefreshTokenService refreshTokenService;
    private final ValidAccountTokenService validAccountTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final EmailService emailService;

    @Value("${letsplay.app.url}")
    private String appUrl;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UserService userService, RoleService roleService, RefreshTokenService refreshTokenService,
                          ValidAccountTokenService validAccountTokenService, ResetPasswordTokenService resetPasswordTokenService,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.roleService = roleService;
        this.refreshTokenService = refreshTokenService;
        this.validAccountTokenService = validAccountTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserInfoResponse getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return new UserInfoResponse(user.getId(), user.getName(), user.getProfilePicture());
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody SignupRequest userInfos) {
        try {
            if (!userService.existsByEmail(userInfos.email()) && !userService.existsByUserName(userInfos.name())) {
                Role userRole = roleService.findByName("ROLE_USER");

                User user = new User(UUID.randomUUID(), userInfos.name(), userInfos.email(), userInfos.password(), userInfos.profilePicture(),
                        false, userRole, null, null);

                userService.createUser(user);
                LOGGER.info("User created: {}", user.getName());

                ValidAccountToken validationToken = validAccountTokenService.createValidationToken(user);

                sendValidationEmail(user, validationToken);

                LOGGER.info("Validation email sent to: {}", user.getEmail(), " with token: {}", validationToken.getToken());

                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                LOGGER.error("User already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (final Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOkResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.name(), loginRequest.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserInfo userDetails = (UserInfo) authentication.getPrincipal();

            String jwt = jwtService.generateJwtToken(userDetails.getUsername());

            final List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            User connecterUser = userService.getUserByEmail(userDetails.getUsername());

            if (!connecterUser.isValid()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());


            LOGGER.info("User {} / {} logged in", userDetails.getUsername(), connecterUser.getName());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwt)
                    .body(new LoginOkResponse(refreshToken.getToken(), connecterUser.getId(), connecterUser.getName(),
                            connecterUser.getProfilePicture(), roles));
        } catch (AccessDeniedException e) {
            LOGGER.error("Error authenticating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletRequest request) {
        try {
            ResponseCookie cookie = jwtService.getCleanJwtCookie();

            User connectedUser = userService.getUserFromRequest(request);
            refreshTokenService.deleteByUserId(connectedUser.getId());

            LOGGER.info("User {} logged out", connectedUser.getName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
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
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (AccessDeniedException e) {
            LOGGER.error("Invalid refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/verify/{token}")
    public ResponseEntity<Void> validateUser(@PathVariable("token") String token) {
        try {
            ValidAccountToken checkedToken = validAccountTokenService.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Token not found"));

            User user = checkedToken.getUser();

            user.setValid(true);
            userService.updateUser(user.getId(), user);

            validAccountTokenService.deleteByToken(token);

            LOGGER.info("User validated: {}", user.getName());

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (final Exception e) {
            LOGGER.error("Error validating user: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<Void> forgotPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        User user = userService.getUserByEmail(passwordResetRequest.email());
        if (user != null) {
            ResetPasswordToken resetToken = resetPasswordTokenService.createResetPasswordToken(user);
            try {
                sendForgotPasswordEmail(user.getEmail(), appUrl + "/resetpassword/" + resetToken.getToken());
                LOGGER.info("Forgot password email sent to: {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (MessagingException e) {
                LOGGER.error("Error sending forgot password email: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/resetpassword/{token}")
    public ResponseEntity<Void> resetPassword(@PathVariable String token, @RequestBody NewPasswordRequest request) {
        try {
            ResetPasswordToken checkedToken = resetPasswordTokenService.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Token not found"));

            User user = checkedToken.getUser();
            userService.updateUserPassword(user.getId(), request.password());

            resetPasswordTokenService.deleteByToken(token);

            LOGGER.info("Password reset for user: {}", user.getName());

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (final Exception e) {
            LOGGER.error("Error resetting password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @RequestBody String password, HttpServletRequest request) {
        if (isUserAuthorizedToModify(id, request))
            try {
                userService.updateUserPassword(id, password);
                return ResponseEntity.ok().build();
            } catch (final Exception e) {
                LOGGER.error("Error modifying password: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest updateRequest, HttpServletRequest request) {
        if (isUserAuthorizedToModify(id, request)) {
            try {
                User user = userService.getUserById(id);
                user.setName(updateRequest.name());
                user.setProfilePicture(updateRequest.profilePicture());
                userService.updateUser(id, user);
                return ResponseEntity.ok(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getProfilePicture()));
            } catch (final Exception e) {
                LOGGER.error("Error updating user: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, HttpServletRequest request) {

        if (isUserAuthorizedToModify(id, request)) {
            try {
                userService.deleteUser(id);
                return ResponseEntity.ok().build();
            } catch (final Exception e) {
                LOGGER.error("Error deleting user: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private boolean isUserAuthorizedToModify(UUID userId, HttpServletRequest request) {
        User connectedUser = userService.getUserFromRequest(request);
        return connectedUser.getId().equals(userId);
    }

    private void sendValidationEmail(User user, ValidAccountToken validationToken) throws MessagingException {
        String welcomeMessage = String.format("Bienvenue sur Let's Play, %s !", user.getName());
        String validationLink = appUrl + "/verify/" + validationToken.getToken();

        emailService.sendConfirmEmail(user.getEmail(), "Validation de ton compte Let's Play",
                welcomeMessage, "Pour valider ton compte, clique sur le lien suivant : " +
                        "\n Attention, ce lien est valable 24h ! \n",
                validationLink, "Valider mon compte");
    }

    private void sendForgotPasswordEmail(String userEmail, String url) throws MessagingException {
        emailService.sendConfirmEmail(userEmail, "Réinitialisation de ton mot de passe Let's Play",
                "Alors, on a oublié son mot de passe ?", "Pour réinitialiser ton mot de passe, clique sur le lien suivant :",
                url, "Je définis un nouveau mot de passe");
    }
}