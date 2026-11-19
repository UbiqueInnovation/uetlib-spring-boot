package ch.uetlib.spring.boot.security.manager;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ApiKeyAuthenticationManager implements AuthenticationManager {

    // A list, not a map: arrays have identity equals, and keys are compared with
    // MessageDigest.isEqual.
    private final List<Entry<byte[], Collection<? extends GrantedAuthority>>> apiKeys;

    public ApiKeyAuthenticationManager(final String appApiKey, final String authority) {
        this(
                Map.of(
                        requireNonNull(appApiKey, "appApiKey must not be null"),
                        List.of(
                                new SimpleGrantedAuthority(
                                        requireNonNull(authority, "authority must not be null")))));
    }

    public ApiKeyAuthenticationManager(
            final Map<String, ? extends Collection<? extends GrantedAuthority>> apiKeyMap) {
        requireNonNull(apiKeyMap, "apiKeyMap must not be null");
        this.apiKeys =
                apiKeyMap.entrySet().stream()
                        .map(
                                entry -> {
                                    final var apiKeyBytes =
                                            requireNonNull(
                                                            entry.getKey(),
                                                            "API key must not be null")
                                                    .getBytes(UTF_8);
                                    final var authorities =
                                            requireNonNull(
                                                    entry.getValue(),
                                                    "authorities must not be null");
                                    return Map
                                            .<byte[], Collection<? extends GrantedAuthority>>entry(
                                                    apiKeyBytes, authorities);
                                })
                        .toList();
    }

    @Override
    public Authentication authenticate(final Authentication authentication)
            throws AuthenticationException {
        final var principal = authentication.getPrincipal();
        if (principal instanceof final String requestApiKey) {
            final var requestApiKeyBytes = requestApiKey.getBytes(UTF_8);

            final var matchedAuthorities =
                    apiKeys.stream()
                            .filter(
                                    entry ->
                                            MessageDigest.isEqual(
                                                    entry.getKey(), requestApiKeyBytes))
                            .map(Entry::getValue)
                            .findFirst();
            if (matchedAuthorities.isPresent()) {
                final var authenticatedToken =
                        new PreAuthenticatedAuthenticationToken(
                                principal,
                                authentication.getCredentials(),
                                matchedAuthorities.get());
                authenticatedToken.setAuthenticated(true);
                return authenticatedToken;
            }
        }

        throw new BadCredentialsException("Invalid API key");
    }
}
