package org.springy.som.modulith.config;

import lombok.extern.slf4j.Slf4j;
import org.springy.som.security.KeycloakTokenClient;
import org.springy.som.security.KeycloakTokenRefresher;
import org.springy.som.security.KeycloakRefreshProps;
import org.springy.som.security.SomProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.security.PrivateKey;

@Configuration
@Slf4j
public class SomAuthClientConfig {
    private final RestClient.Builder restClientBuilder;
    public SomAuthClientConfig(RestClient.Builder builder) {
        this.restClientBuilder = builder;
    }

    @Bean
    @ConditionalOnBean({SomProps.class, PrivateKey.class, KeycloakRefreshProps.class})
    public KeycloakTokenClient keycloakTokenClient(SomProps somProps, PrivateKey privateKey, KeycloakRefreshProps refreshProps,
                                            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        log.info("Creating KeycloakTokenClient");
        return new KeycloakTokenClient(restClientBuilder, somProps, privateKey, refreshProps, issuerUri);
    }

    @Bean
    @ConditionalOnBean(KeycloakTokenClient.class)
    public KeycloakTokenRefresher keycloakTokenRefresher(KeycloakTokenClient keycloakTokenClient) {
        return new KeycloakTokenRefresher(keycloakTokenClient);
    }
}
