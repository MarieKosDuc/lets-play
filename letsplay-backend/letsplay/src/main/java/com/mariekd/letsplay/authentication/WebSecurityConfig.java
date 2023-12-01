package com.mariekd.letsplay.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.authorizeRequests(auth ->
                        auth
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/admin").hasRole("ADMIN")
                // toutes les autres routes sont soumises à autorisation :
                // .anyRequest().authenticated()
                );

         return http.build();
    }
}
