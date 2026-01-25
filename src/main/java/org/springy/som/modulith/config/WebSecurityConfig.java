package org.springy.som.modulith.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnBooleanProperty(name = "som.web-security.enabled")
public class WebSecurityConfig {
//    @Bean
//    @Order(1)
//    SecurityFilterChain readOnlyChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter) throws Exception {
//        http.securityMatcher("/api/v1/**")
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth.anyRequest().hasAuthority("ROLE_som-read-only"))
//            .oauth2ResourceServer(oauth2 -> oauth2
//            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
//        return http.build();
//    }

    @Bean
//    @Order(2)
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

