package ch.ubique.backend.security.filter;

import static java.util.Objects.requireNonNull;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String authorizationHeaderName;

    public ApiKeyAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this(authenticationManager, HttpHeaders.AUTHORIZATION);
    }

    public ApiKeyAuthenticationFilter(
            final AuthenticationManager authenticationManager,
            final String authorizationHeaderName) {
        this.authorizationHeaderName =
                requireNonNull(authorizationHeaderName, "headerName must not be null");
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected @Nullable Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        return request.getHeader(authorizationHeaderName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
        return "N/A";
    }
}
