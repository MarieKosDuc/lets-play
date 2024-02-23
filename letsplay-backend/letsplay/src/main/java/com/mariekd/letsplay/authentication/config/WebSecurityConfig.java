package com.mariekd.letsplay.authentication.config;

import com.mariekd.letsplay.authentication.jwt.JwtAuthenticationFilter;
import com.mariekd.letsplay.authentication.services.UserService;
import com.mariekd.letsplay.authentication.services.implementations.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {


    private final PasswordEncoderConfig passwordEncoderConfig;

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        return jwtAuthenticationFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(final UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // set the custom user details service
        authProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder()); // encodes password
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
                        .requestMatchers("/api/users/register").permitAll() // à modifier pour que seule la création soit accessible
                        .requestMatchers("/api/users/login").permitAll() // à modifier pour que seule la création soit accessible
                        .requestMatchers("/api/users/all").permitAll()
                        .anyRequest().authenticated()

                )
                .csrf(AbstractHttpConfigurer::disable
                );
        http.authenticationProvider(authenticationProvider(userDetailsService));
        return http.build();
    }



}