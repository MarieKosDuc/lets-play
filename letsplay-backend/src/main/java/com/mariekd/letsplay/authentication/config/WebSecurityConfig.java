package com.mariekd.letsplay.authentication.config;

import com.mariekd.letsplay.authentication.enums.RolesEnum;
import com.mariekd.letsplay.authentication.jwt.JwtAuthenticationFilter;
import com.mariekd.letsplay.authentication.services.implementations.UserDetailsServiceImpl;
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
public class WebSecurityConfig {

    private final List<AntPathRequestMatcher> excludedMatchers = List.of(
            new AntPathRequestMatcher("/api/users/register"),
            new AntPathRequestMatcher("/api/users/login"),
            new AntPathRequestMatcher("/api/users/verify/**"),
            new AntPathRequestMatcher("/api/users/resetpassword"),
            new AntPathRequestMatcher("/api/users/resetpassword/**"),
            new AntPathRequestMatcher("/api/users/refreshtoken"),
            new AntPathRequestMatcher("/api/users/logout"),
            new AntPathRequestMatcher("/api/ads/get/**"),
            new AntPathRequestMatcher("/api/ads/search"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**")
    );

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    
    public WebSecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

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
                        .requestMatchers("/api/users/verify/**").permitAll()
                        .requestMatchers("/api/users/resetpassword").permitAll()
                        .requestMatchers("/api/users/resetpassword/**").permitAll()
                        .requestMatchers("/api/users/logout").permitAll()
                        .requestMatchers("/api/ads/get/**").permitAll()
                        .requestMatchers("/api/ads/search").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(RolesEnum.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable);

        http.authenticationProvider(authenticationProvider(userDetailsService));

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
