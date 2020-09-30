package ch.ubique.backend.core.security;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** LogRequestInterceptor for logging user activity. */
public class LogRequestInterceptor implements HandlerInterceptor, Filter {

    private static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    private static final Logger logger = LoggerFactory.getLogger(LogRequestInterceptor.class);

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView)
            throws Exception {
        log(request, response);
    }

    private void log(HttpServletRequest request, HttpServletResponse response) {
        var ipAddress =
                Optional.ofNullable(request.getHeader(X_FORWARDED_FOR_HEADER))
                        .orElse(request.getRemoteAddr());
        logger.info(
                String.format(
                        "Request: %s | Method: %s | HTTP Status: %d | IP: %s",
                        request.getRequestURI(),
                        request.getMethod(),
                        response.getStatus(),
                        ipAddress));
    }

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            var httpServletRequest = (HttpServletRequest) request;
            var httpServletResponse = (HttpServletResponse) response;
            log(httpServletRequest, httpServletResponse);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {}
}
