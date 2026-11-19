package ch.uetlib.spring.boot.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.MessageFormat;

// TODO remove in 3.0.0
/**
 * @deprecated since 2.4.0, migrate to {@link
 *     ch.uetlib.spring.boot.security.filter.ApiKeyAuthenticationFilter} and {@link
 *     ch.uetlib.spring.boot.security.manager.ApiKeyAuthenticationManager} in {@code
 *     uetlib-spring-boot-security}.
 *     <p>See <a
 *     href="https://github.com/UbiqueInnovation/uetlib-spring-boot/blob/main/CHANGELOG.md#240">Changelog
 *     2.4.0</a>
 *     <p>Will be removed in `3.0.0`.
 */
@SuppressWarnings("unused")
@Deprecated(forRemoval = true, since = "2.4.0")
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String appApiKey;

    public ApiKeyAuthenticationFilter(final String appApiKey) {
        this.appApiKey = appApiKey;
    }

    @Override
    protected final void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {

        final var requestApiKey = request.getHeader("X-API-KEY");
        if (MessageDigest.isEqual(appApiKey.getBytes(UTF_8), requestApiKey.getBytes(UTF_8))) {
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
