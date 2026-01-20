package ch.ubique.backend.spring.boot.problem.handler;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings("unused")
@NullMarked
public abstract class UetlibExceptionHandler extends ResponseEntityExceptionHandler
        implements UetlibExceptionHandlerSkeleton,
                ThrowableProblemAdviceTrait,
                MoreExceptionsExceptionAdviceTrait,
                RestClientExceptionAdviceTrait {

    private static final Logger logger = LoggerFactory.getLogger(UetlibExceptionHandler.class);

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public @Nullable MessageSource getMessageSource() {
        return super.getMessageSource();
    }

    /**
     * This method is defined as {@code public} in {@link UetlibExceptionHandlerSkeleton} but
     * implemented as {@code protected} in {@link ResponseEntityExceptionHandler}. We have to
     * redefine it as {@code public} here and call the super method, otherwise Java won't compile
     * it.
     */
    @Override
    public ProblemDetail createProblemDetail(
            final Exception ex,
            final HttpStatusCode status,
            final String defaultDetail,
            @Nullable final String detailMessageCode,
            final Object @Nullable [] detailMessageArguments,
            final WebRequest request) {
        return super.createProblemDetail(
                ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
    }

    /**
     * This method is defined as {@code public} in {@link UetlibExceptionHandlerSkeleton} but
     * implemented as {@code protected} in {@link ResponseEntityExceptionHandler}. We have to
     * redefine it as {@code public} here and call the super method, otherwise Java won't compile
     * it.
     */
    @Override
    public @Nullable ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            @Nullable final Object body,
            final HttpHeaders headers,
            final HttpStatusCode statusCode,
            final WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
