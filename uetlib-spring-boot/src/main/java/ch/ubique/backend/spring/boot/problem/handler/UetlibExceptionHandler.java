package ch.ubique.backend.spring.boot.problem.handler;

import static java.util.Objects.requireNonNull;

import ch.ubique.backend.spring.boot.problem.handler.trait.MoreExceptionsExceptionAdviceTrait;
import ch.ubique.backend.spring.boot.problem.handler.trait.RestClientExceptionAdviceTrait;
import ch.ubique.backend.spring.boot.problem.handler.trait.ThrowableProblemAdviceTrait;
import ch.ubique.backend.spring.boot.problem.handler.trait.violation.ViolationAdviceTrait;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings("unused")
@NullMarked
@EnableConfigurationProperties(UetlibSpringBootProblemProperties.class)
public abstract class UetlibExceptionHandler extends ResponseEntityExceptionHandler
        implements UetlibExceptionHandlerSkeletonTrait,
                ThrowableProblemAdviceTrait,
                ViolationAdviceTrait,
                MoreExceptionsExceptionAdviceTrait,
                RestClientExceptionAdviceTrait {

    @SuppressWarnings("java:S2387")
    private static final Logger logger = LoggerFactory.getLogger(UetlibExceptionHandler.class);

    @Nullable private ApplicationContext applicationContext;
    private boolean includeConstraintViolations;

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public @Nullable MessageSource getMessageSource() {
        return super.getMessageSource();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        includeConstraintViolations =
                applicationContext
                        .getBean(UetlibSpringBootProblemProperties.class)
                        .isIncludeConstraintViolations();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return requireNonNull(applicationContext);
    }

    /**
     * This method is defined as {@code public} in {@link UetlibExceptionHandlerSkeletonTrait} but
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
     * This method is defined as {@code public} in {@link UetlibExceptionHandlerSkeletonTrait} but
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

    /**
     * This method is defined as {@code public} in {@link ResponseEntityExceptionHandler} but we
     * want to override it in {@link MethodArgumentNotValidException} (an interface, thus public)
     */
    @Nullable
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {
        if (includeConstraintViolations) {
            return ViolationAdviceTrait.super.handleMethodArgumentNotValid(
                    ex, headers, status, request);
        }

        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
