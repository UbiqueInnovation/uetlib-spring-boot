package ch.ubique.backend.spring.boot.problem.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/** Logs exceptions left unresolved by MVC without claiming to have handled them. */
public final class UnhandledExceptionLoggingResolver implements HandlerExceptionResolver, Ordered {
    private final UetlibExceptionHandler exceptionHandler;

    public UnhandledExceptionLoggingResolver(final UetlibExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public @Nullable ModelAndView resolveException(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final @Nullable Object handler,
            final Exception ex) {
        exceptionHandler.logException(ex, null, new ServletWebRequest(request, response));
        return null;
    }
}
