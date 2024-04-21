package com.mariekd.letsplay.authentication.config;

import com.mariekd.letsplay.authentication.jwt.JwtAuthenticationFilter;
import com.mariekd.letsplay.authentication.services.implementations.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final List<AntPathRequestMatcher> excludedMatchers = List.of(
            new AntPathRequestMatcher("/api/users/register"),
            new AntPathRequestMatcher("/api/users/login"),
            new AntPathRequestMatcher("/api/users/refreshtoken"),
            new AntPathRequestMatcher("/api/users/logout"),
            new AntPathRequestMatcher("/api/ads/get/**"),
            new AntPathRequestMatcher("/api/ads/search")
    );

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Bean
    public PasswordEncoder passwordEncoder () {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(excludedMatchers, resolver);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(final UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // sets the custom user details service
        authProvider.setPasswordEncoder(passwordEncoder()); // encodes password
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsServiceImpl userDetailsService) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/refreshtoken").permitAll()
                        .requestMatchers("/api/users/logout").permitAll()
                        .requestMatchers("/api/ads/get/**").permitAll()
                        .requestMatchers("/api/ads/search").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable);

        http.authenticationProvider(authenticationProvider(userDetailsService));

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



}
