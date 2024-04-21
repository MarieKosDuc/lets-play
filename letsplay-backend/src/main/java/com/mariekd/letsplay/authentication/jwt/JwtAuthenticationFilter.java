package com.mariekd.letsplay.authentication.jwt;

import com.mariekd.letsplay.authentication.entities.RefreshToken;
import com.mariekd.letsplay.authentication.payload.request.TokenRefreshRequest;
import com.mariekd.letsplay.authentication.payload.response.TokenRefreshResponse;
import com.mariekd.letsplay.authentication.services.RefreshTokenService;
import com.mariekd.letsplay.authentication.services.implementations.UserDetailsServiceImpl;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component(role = JwtAuthenticationFilter.class)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if(request.getRequestURL().toString().contains("/api/users/refreshtoken") || request.getRequestURL().toString().contains("/api/users/logout")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String jwt = parseJwt(request);

                if (jwt != null) {
                    JwtValidationResult validationResult = jwtService.validateAndCheckExpirationJwtToken(jwt);

                    if (validationResult.isValid() && validationResult.isExpired()) {
//                    if (!request.getRequestURL().toString().contains("/api/users/refreshtoken")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                        return;
//                    } else {
//                        TokenRefreshRequest refreshReq = new TokenRefreshRequest(request.getParameter("token"));
//                        refreshtoken(new TokenRefreshRequest(refreshReq.getToken()));
//                    }
                    } else if (jwtService.validateJwtToken(jwt)) {
                        String email = jwtService.getUserNameFromJwtToken(jwt);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

//            if (jwt != null && jwtService.validateJwtToken(jwt)) {
//                String email = jwtService.getUserNameFromJwtToken(jwt);
//
//                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities()
//                );
//
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }

            filterChain.doFilter(request, response);
        }

    }

    private String parseJwt(HttpServletRequest request) {
        return jwtService.getJwtFromCookies(request);
    }

    private ResponseEntity<?> refreshtoken(TokenRefreshRequest request) {
        LOGGER.info("Refreshing token: {} ", request.getToken());
        String requestRefreshToken = request.getToken();

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

}
