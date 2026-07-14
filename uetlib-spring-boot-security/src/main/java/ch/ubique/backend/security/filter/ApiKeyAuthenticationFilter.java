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
    private final String @Nullable [] authorizationHeaderPrefixes;

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
        this(
                authenticationManager,
                authorizationHeaderName,
                HttpHeaders.AUTHORIZATION.equalsIgnoreCase(authorizationHeaderName)
                        ? new String[] {"ApiKey "}
                        : null);
    }

    /**
     * Creates a new filter using a custom header name and custom prefix format.
     *
     * <p>If the prefix is non-null and non-empty, it expects the header value to match this prefix
     * (case-insensitively) and returns the substring following the prefix. If the prefix is null or
     * empty, it returns the raw header value directly.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationHeaderName the name of the header to check
     * @param authorizationHeaderPrefixes the expected prefixes for the key (e.g. {@code "ApiKey "})
     */
    public ApiKeyAuthenticationFilter(
            final AuthenticationManager authenticationManager,
            final String authorizationHeaderName,
            final String @Nullable ... authorizationHeaderPrefixes) {
        this.authorizationHeaderName =
                requireNonNull(authorizationHeaderName, "headerName must not be null");
        this.authorizationHeaderPrefixes = authorizationHeaderPrefixes;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected @Nullable Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        final var headerValue = request.getHeader(authorizationHeaderName);
        if (headerValue == null || authorizationHeaderPrefixes == null) {
            return headerValue;
        }

        for (final var authorizationHeaderKeyPrefix : authorizationHeaderPrefixes) {
            final var prefixLength = authorizationHeaderKeyPrefix.length();
            if (headerValue.regionMatches(true, 0, authorizationHeaderKeyPrefix, 0, prefixLength)) {
                return headerValue.substring(prefixLength);
            }
        }

        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
        return "N/A";
    }
}
