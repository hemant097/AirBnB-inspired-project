package com.example.project.airbnbapp.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
    public class AppConfig {

        //to handle constructor injection in EmployeeService, also to implement DRY
        @Bean
        public ModelMapper createMapper(){
            return new ModelMapper();
        }

        @Bean
        PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder();}

        @Bean
        AuthenticationManager authenticationManager (AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }
    }
