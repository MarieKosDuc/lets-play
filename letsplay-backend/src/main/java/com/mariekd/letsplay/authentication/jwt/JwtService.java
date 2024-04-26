package com.mariekd.letsplay.authentication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;

import java.security.Key;
import java.util.Date;



@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); //TODO : check ERROR 6856 JWT signature is invalid: JWT signature does not match locally computed signature. JWT validity cannot be asserted

    private final String jwtCookieName = "jwt";

    public String generateJwtToken(String userName ) {

        final String jwt = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 600000)) // token validity : 10 minutes
                .signWith(this.secretKey, SignatureAlgorithm.HS512)
                .compact();

        return ResponseCookie.from(jwtCookieName, jwt)
                .httpOnly(true)
//                .sameSite("None")
                .secure(false)
                .maxAge(600) // 10 minutes
                .path("/")
                .build()
                .toString();

    }

    public String getUserNameFromJwtToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public JwtValidationResult validateAndCheckExpirationJwtToken(String authToken) {
        JwtValidationResult validationResult = new JwtValidationResult();
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
            validationResult.setValid(true);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            validationResult.setValid(false);
            validationResult.setErrorMessage("Invalid JWT token");
        } catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
            validationResult.setValid(true);
            validationResult.setExpired(true);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            validationResult.setValid(false);
            validationResult.setErrorMessage("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            validationResult.setValid(false);
            validationResult.setErrorMessage("JWT claims string is empty");
        } catch (SignatureException e) {
            logger.error("JWT signature is invalid: {}", e.getMessage());
            validationResult.setValid(false);
            validationResult.setErrorMessage("Invalid JWT signature");
        }
        return validationResult;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        final Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
