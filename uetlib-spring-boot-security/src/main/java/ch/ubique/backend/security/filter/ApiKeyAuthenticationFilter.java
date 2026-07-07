package ch.ubique.backend.security.filter;

import static java.util.Objects.requireNonNull;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * An authentication filter that extracts an API Key from an HTTP request header.
 *
 * <p>If configured with the default header {@link HttpHeaders#AUTHORIZATION} (case-insensitive), it
 * expects the header value to be prefixed with {@code "ApiKey "}, e.g. {@code "ApiKey <token>"},
 * and extracts only the token. If the prefix is missing or incorrect, it returns {@code null} to
 * skip pre-authentication.
 *
 * <p>If configured with any other custom header name (such as {@code "X-API-KEY"} or {@code
 * "ApiKey"}), it will extract and return the whole header value directly without any prefix
 * validation or parsing.
 */
public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String authorizationHeaderName;

    /**
     * Creates a new filter using {@link HttpHeaders#AUTHORIZATION} as the header name. Expects
     * values in the format {@code "ApiKey <token>"}.
     *
     * @param authenticationManager the authentication manager
     */
    public ApiKeyAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this(authenticationManager, HttpHeaders.AUTHORIZATION);
    }

    /**
     * Creates a new filter using a custom header name.
     *
     * <p>If the specified header name is {@link HttpHeaders#AUTHORIZATION} (case-insensitive), it
     * expects values in the format {@code "ApiKey <token>"}. If the header name is any other value
     * (e.g. {@code "X-API-KEY"} or {@code "ApiKey"}), it extracts the entire value of that header
     * directly.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationHeaderName the name of the header to check
     */
    public ApiKeyAuthenticationFilter(
            final AuthenticationManager authenticationManager,
            final String authorizationHeaderName) {
        this.authorizationHeaderName =
                requireNonNull(authorizationHeaderName, "headerName must not be null");
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected @Nullable Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        final var headerValue = request.getHeader(authorizationHeaderName);
        if (headerValue == null) {
            return null;
        }
        if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(authorizationHeaderName)) {
            if (headerValue.regionMatches(true, 0, "ApiKey ", 0, 7)) {
                return headerValue.substring(7);
            }
            return null;
        }
        return headerValue;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
        return "N/A";
    }
}
