package com.example.project.airbnbapp.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.project.airbnbapp.Entity.enums.Role.HOTEL_MANAGER;

@Configuration  @EnableWebSecurity  @RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception{

        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement( sessionConfig -> sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers("/admin/**").hasRole(HOTEL_MANAGER.name())
                        .requestMatchers("/bookings/**").authenticated()
                        .anyRequest().permitAll())

                ;

        return httpSecurity.build();
    }
}
