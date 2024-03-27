package ch.ubique.backend.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Override {@link #shouldNotFilter} to configure which requests are filtered. For path-based
 * filtering you get the request path from {@link HttpServletRequest#getServletPath()}.
 */
@SuppressWarnings("unused")
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final String appApiKey;

    public ApiKeyAuthenticationFilter(final String appApiKey) {
        this.appApiKey = appApiKey;
    }

    @Override
    protected final void doFilterInternal(
            final HttpServletRequest request,
            final @NotNull HttpServletResponse response,
            final @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        final var requestApiKey = request.getHeader("X-API-KEY");
        if (appApiKey.equals(requestApiKey)) {
            logger.debug(
                    MessageFormat.format(
                            "Authorized for servletPath: {0}", request.getServletPath()));
            filterChain.doFilter(request, response);
        } else {
            logger.warn(
                    MessageFormat.format(
                            "Unauthorized for servletPath: {0}", request.getServletPath()));
            // Reject the request and send an unauthorized error
            response.getWriter().write("Unauthorized");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
