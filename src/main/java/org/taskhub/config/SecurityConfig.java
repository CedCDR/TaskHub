package org.taskhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Wir stellen den BCrypt-Encoder als globales Werkzeug (Bean) zur Verfügung
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Wir schalten die automatische Blockade vorerst ab
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Für APIs oft deaktiviert (wir nutzen später Token)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Erlaubt vorerst ALLE Anfragen ohne Login
                );
        return http.build();
    }
}