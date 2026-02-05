//package org.springy.som.modulith.security;
//
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.time.Instant;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Component
//public class KeycloakTokenClient {
//    private final WebClient webClient;
//    private final SomProps props;
//    private final SecretDecryptor decryptor;
//    private final KeycloakRefreshProps refreshProps;
//
//    private final AtomicReference<CachedToken> cache = new AtomicReference<>();
//    private final ReentrantLock refreshLock = new ReentrantLock();
//
//    public KeycloakTokenClient(WebClient.Builder builder, SomProps props,
//            SecretDecryptor decryptor, KeycloakRefreshProps refreshProps) {
//        this.props = props;
//        this.decryptor = decryptor;
//        this.refreshProps = refreshProps;
//        this.webClient = builder.build();
//    }
//
//    public String getAccessToken() {
//        CachedToken existing = cache.get();
//        if (existing != null && existing.isValid()) return existing.token();
//
//        ensureFreshTokenOrThrow();
//        CachedToken now = cache.get();
//        if (now == null || !now.isValid()) {
//            throw new IllegalStateException("No valid Keycloak token available");
//        }
//        return now.token();
//    }
//
//    public void ensureFreshTokenOrThrow() {
//        if (!refreshLock.tryLock()) {
//            waitForOtherRefresher();
//            return;
//        }
//        try {
//            for (int attempt = 1; attempt <= refreshProps.maxAttempts(); attempt++) {
//                try {
//                    CachedToken current = cache.get();
//                    if (current != null && current.isValid()) return;
//
//                    TokenResponse tr = requestClientCredentialsToken();
//                    cache.set(new CachedToken(tr.access_token(), expiryInstant(tr.expires_in())));
//                    return;
//                } catch (Exception e) {
//                    sleep(refreshProps.backoffMs() * attempt);
//                }
//            }
//        } finally {
//            refreshLock.unlock();
//        }
//    }
//
//    public boolean needsRefreshSoon() {
//        CachedToken t = cache.get();
//        if (t == null) return true;
//        return Instant.now().isAfter(t.expiresAt().minusSeconds(refreshProps.skewSeconds()));
//    }
//
//    public void refreshIfNeeded() {
//        if (!needsRefreshSoon()) return;
//        if (!refreshLock.tryLock()) return;
//        try {
//            if (!needsRefreshSoon()) return;
//
//            TokenResponse tr = requestClientCredentialsToken();
//            cache.set(new CachedToken(tr.access_token(), expiryInstant(tr.expires_in())));
//        } finally {
//            refreshLock.unlock();
//        }
//    }
//
//    private Instant expiryInstant(long expiresInSeconds) {
//        long skew = Math.max(0, refreshProps.skewSeconds());
//        long effective = Math.max(1, expiresInSeconds - skew);
//        return Instant.now().plusSeconds(effective);
//    }
//
//    private void waitForOtherRefresher() {
//        long deadline = System.currentTimeMillis() + refreshProps.pollMs();
//        while (System.currentTimeMillis() < deadline) {
//            CachedToken t = cache.get();
//            if (t != null && t.isValid()) return;
//            sleep(25);
//        }
//    }
//
//    private TokenResponse requestClientCredentialsToken() {
//        String tokenUrl = props.keycloak().baseUrl()
//                + "/realms/" + props.keycloak().realm()
//                + "/protocol/openid-connect/token";
//
//        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
//        form.add("grant_type", "client_credentials");
//        form.add("client_id", props.keycloak().clientId());
//        form.add("client_secret", decryptor.decryptIfEncrypted(props.keycloak().clientSecret()));
//
//        return webClient.post()
//                .uri(tokenUrl)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue(form)
//                .retrieve()
//                .bodyToMono(TokenResponse.class)
//                .block();
//    }
//
//    private static void sleep(long ms) {
//        try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
//    }
//
//    public record TokenResponse(String access_token, long expires_in, String token_type) {}
//
//    public record CachedToken(String token, Instant expiresAt) {
//        public boolean isValid() { return Instant.now().isBefore(expiresAt); }
//    }
//}
