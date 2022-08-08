package com.example.kuberneteswebsocketintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
          .anyRequest().permitAll();
        
        // http
        //     .authorizeHttpRequests((authz) -> authz
        //         .anyRequest().authenticated()
        //     )
        //     .httpBasic(withDefaults());
        return http.build();
    }

}

