package ch.ubique.uetlib.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.ubique.uetlib.security.manager.ApiKeyAuthenticationManager;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@SuppressWarnings("DataFlowIssue")
class ApiKeyAuthenticationManagerTest {

    private static final String APP_API_KEY = "super-secret-api-key";
    private static final String AUTHORITY = "ROLE_DEMO";

    private final ApiKeyAuthenticationManager manager =
            new ApiKeyAuthenticationManager(APP_API_KEY, AUTHORITY);

    @Test
    void authenticate_withValidKey_returnsAuthenticatedTokenWithAuthority() {
        final var token = new PreAuthenticatedAuthenticationToken(APP_API_KEY, "N/A");

        final var result = manager.authenticate(token);

        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(APP_API_KEY);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(AUTHORITY);
    }

    @Test
    void authenticate_withInvalidKey_throwsBadCredentialsException() {
        final var token = new PreAuthenticatedAuthenticationToken("wrong-key", "N/A");

        assertThatThrownBy(() -> manager.authenticate(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_withNullPrincipal_throwsBadCredentialsException() {
        final var token = new PreAuthenticatedAuthenticationToken(null, "N/A");

        assertThatThrownBy(() -> manager.authenticate(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_withNonStringPrincipal_throwsBadCredentialsException() {
        final var token = new PreAuthenticatedAuthenticationToken(12345, "N/A");

        assertThatThrownBy(() -> manager.authenticate(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_withMapConstructor_matchesCorrectKeys() {
        final var apiKey1 = "api-key-one";
        final var apiKey2 = "api-key-two";
        final var auth1 = new SimpleGrantedAuthority("ROLE_ONE");
        final var auth2 = new SimpleGrantedAuthority("ROLE_TWO");

        final var keyMap =
                Map.of(
                        apiKey1, List.of(auth1),
                        apiKey2, List.of(auth2));
        final var mapManager = new ApiKeyAuthenticationManager(keyMap);

        // Test apiKey1
        final var token1 = new PreAuthenticatedAuthenticationToken(apiKey1, "N/A");
        final var result1 = mapManager.authenticate(token1);
        assertThat(result1).isNotNull();
        assertThat(result1.isAuthenticated()).isTrue();
        assertThat(result1.getPrincipal()).isEqualTo(apiKey1);
        assertThat(result1.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ONE")
                .doesNotContain("ROLE_TWO");

        // Test apiKey2
        final var token2 = new PreAuthenticatedAuthenticationToken(apiKey2, "N/A");
        final var result2 = mapManager.authenticate(token2);
        assertThat(result2).isNotNull();
        assertThat(result2.isAuthenticated()).isTrue();
        assertThat(result2.getPrincipal()).isEqualTo(apiKey2);
        assertThat(result2.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_TWO")
                .doesNotContain("ROLE_ONE");

        // Test wrong key
        final var wrongToken = new PreAuthenticatedAuthenticationToken("wrong-key", "N/A");
        assertThatThrownBy(() -> mapManager.authenticate(wrongToken))
                .isInstanceOf(BadCredentialsException.class);
    }
}
