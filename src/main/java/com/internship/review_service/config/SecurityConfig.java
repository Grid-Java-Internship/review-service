package com.internship.review_service.config;

import com.internship.authentication_library.config.SecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityConfiguration securityConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        String[] lista1={"/v1/review/**"};
        String[] lista2={"/v1/review/edit"};
        String[] lista3={"/v1/review/entity/**"};

        return securityConfiguration.securityFilterChain(http,lista1,lista2,lista3);
    }


}
