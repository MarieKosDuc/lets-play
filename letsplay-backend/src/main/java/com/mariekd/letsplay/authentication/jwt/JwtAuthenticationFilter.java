package com.mariekd.letsplay.authentication.jwt;

import com.mariekd.letsplay.authentication.services.implementations.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component(role = JwtAuthenticationFilter.class)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final List<AntPathRequestMatcher> excludedMatchers;

    private final HandlerExceptionResolver resolver;


    @Autowired
    private  JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(List<AntPathRequestMatcher> excludedMatchers, HandlerExceptionResolver resolver) {
        this.excludedMatchers = excludedMatchers;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException, UnauthorizedException {
//        if(!request.getRequestURL().toString().contains("/api/users/refreshtoken") //) {
//                || !request.getRequestURL().toString().contains("/api/users/logout")) {
            try {
                String jwt = parseJwt(request);

                if (jwt != null) {
                    JwtValidationResult validationResult = jwtService.validateAndCheckExpirationJwtToken(jwt);

                    if (validationResult.isValid() && validationResult.isExpired()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        throw new UnauthorizedException("Unauthorized: JWT token is expired");
                    } else if (validationResult.isValid()) {
                        String email = jwtService.getUserNameFromJwtToken(jwt);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(request, response);

            } catch(UnauthorizedException e) {
                LOGGER.error("Unauthorized exception: " + e.getMessage());
                resolver.resolveException(request, response, null, e);
            }

            catch (Exception e) {
                LOGGER.error("Cannot set user authentication: {}", e.getMessage());
                resolver.resolveException(request, response, null, e);
            }
        }

    private String parseJwt(HttpServletRequest request) {
        return jwtService.getJwtFromCookies(request);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
