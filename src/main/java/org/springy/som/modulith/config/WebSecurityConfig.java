package org.springy.som.modulith.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnBooleanProperty(name = "som.web-security.enabled")
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain apiChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers("/ops/**").hasAuthority("ROLE_somadmin")
            .requestMatchers("/gm/**").hasAnyAuthority("ROLE_game-master", "ROLE_somadmin")
            .requestMatchers("/api/v1/**").hasAnyAuthority("ROLE_somplayer", "ROLE_game-master", "ROLE_somadmin")
            .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        return http.build();
    }
}

