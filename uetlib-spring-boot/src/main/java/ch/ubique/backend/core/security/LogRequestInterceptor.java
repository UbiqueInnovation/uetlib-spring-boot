package ch.ubique.backend.core.security;

import static java.util.Objects.requireNonNullElse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/** LogRequestInterceptor for logging user activity. */
@SuppressWarnings("unused")
public class LogRequestInterceptor implements HandlerInterceptor, Filter {

    private static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    private static final Logger logger = LoggerFactory.getLogger(LogRequestInterceptor.class);

    @Override
    public void postHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final @Nullable ModelAndView modelAndView) {
        log(request, response);
    }

    private void log(final HttpServletRequest request, final HttpServletResponse response) {
        final var ipAddress =
                requireNonNullElse(
                        request.getHeader(X_FORWARDED_FOR_HEADER), request.getRemoteAddr());
        logger.info(
                "Request: {} | Method: {} | HTTP Status: {} | IP: {}",
                request.getRequestURI(),
                request.getMethod(),
                response.getStatus(),
                ipAddress);
    }

    @Override
    public void doFilter(
            final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof final HttpServletRequest httpServletRequest
                && response instanceof final HttpServletResponse httpServletResponse) {
            log(httpServletRequest, httpServletResponse);
        }
        chain.doFilter(request, response);
    }
}
