package ch.ubique.uetlib.spring.boot.problem.handler;

import static java.util.Objects.requireNonNull;

import ch.ubique.uetlib.spring.boot.problem.handler.trait.violation.Violation;

import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Stream;

/**
 * To implement exception advice traits that can extend the functionality of the {@link
 * UetlibExceptionHandler} in other modules, we define this interface that has the methods from
 * {@link ResponseEntityExceptionHandler}. We have to reimplement the methods in {@link
 * UetlibExceptionHandler} because they are {@code protected} in {@link
 * ResponseEntityExceptionHandler} and interfaces only know {@code public} methods. I wish I could
 * make the traits extend {@link ResponseEntityExceptionHandler} directly, but Java does not allow
 * multiple inheritance, hence all advice traits have to be interfaces.
 */
@SuppressWarnings("JavadocReference")
public interface UetlibExceptionHandlerSkeletonTrait extends ApplicationContextAware {

    Logger logger();

    /**
     * Log exceptions, omitting validation exception messages that can contain rejected values.
     * Override to customize or suppress logging for all advice traits, including validation.
     * Handlers delegating to {@link #handleExceptionInternal} are logged automatically; handlers
     * returning a response directly must call this hook once themselves. Status is null for
     * unmatched exceptions whose eventual response status is determined by the container or
     * filters.
     */
    default void logException(
            final Exception ex, final @Nullable HttpStatusCode status, final WebRequest request) {
        final var method =
                request instanceof final ServletWebRequest servletRequest
                        ? servletRequest.getRequest().getMethod()
                        : "unknown";
        final var path =
                request instanceof final ServletWebRequest servletRequest
                        ? servletRequest.getRequest().getRequestURI()
                        : request.getDescription(false);
        final var statusValue = status == null ? "unknown" : Integer.toString(status.value());
        final var exceptionType = ex.getClass().getSimpleName();
        if (ex instanceof final MethodArgumentNotValidException validationException) {
            final var result = validationException.getBindingResult();
            final var violations =
                    Stream.concat(
                                    result.getFieldErrors().stream().map(Violation::from),
                                    result.getGlobalErrors().stream().map(Violation::from))
                            .map(violation -> violation.fieldName() + ": " + violation.message())
                            .toList();
            final var level =
                    getUetlibSpringBootProblemProperties().isIncludeConstraintViolations()
                            ? Level.WARN
                            : Level.INFO;
            final var executable = validationException.getParameter().getExecutable();
            final var handler =
                    executable.getDeclaringClass().getSimpleName() + "#" + executable.getName();
            logger().atLevel(level)
                    .log(
                            "Request failed: status={} method={} path={} exception={} handler={} violations={}",
                            statusValue,
                            method,
                            path,
                            exceptionType,
                            handler,
                            violations);
            return;
        }
        final var event =
                logger().atLevel(
                                status == null || status.is5xxServerError()
                                        ? Level.ERROR
                                        : Level.WARN);
        if (ex instanceof BindException || ex instanceof ConstraintViolationException) {
            event.log(
                    "Request failed: status={} method={} path={} exception={}",
                    statusValue,
                    method,
                    path,
                    exceptionType);
            return;
        }
        event.setCause(ex)
                .log(
                        "Request failed: status={} method={} path={} exception={} message={}",
                        statusValue,
                        method,
                        path,
                        exceptionType,
                        ex.getMessage());
    }

    @Nullable MessageSource getMessageSource();

    ApplicationContext getApplicationContext();

    UetlibSpringBootProblemProperties getUetlibSpringBootProblemProperties();

    /**
     * A method definition of {@link ResponseEntityExceptionHandler#createProblemDetail(Exception,
     * HttpStatusCode, String, String, Object[], WebRequest)}. Signature has to match the definition
     * exactly
     */
    ProblemDetail createProblemDetail(
            Exception ex,
            HttpStatusCode status,
            String defaultDetail,
            @Nullable String detailMessageCode,
            Object @Nullable [] detailMessageArguments,
            WebRequest request);

    /**
     * A method definition of {@link
     * ResponseEntityExceptionHandler#handleExceptionInternal(Exception, Object, HttpHeaders,
     * HttpStatusCode, WebRequest)}. Delegating custom handlers here applies the shared {@link
     * #logException} policy automatically; do not also log in the calling handler. Signature has to
     * match the definition exactly
     */
    @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request);

    /**
     * Try to map the given status code to an {@link HttpStatusCode} enum value. If there is no
     * matching enum value, the default status code is returned.
     *
     * @param status the status code as integer
     * @param defaultStatusCode the default status code to return if the given status code is
     *     invalid. Must not be {@code null}.
     * @return the corresponding {@link HttpStatusCode} enum value
     */
    default HttpStatusCode resolveStatusCodeOrDefault(
            final int status, final HttpStatusCode defaultStatusCode) {
        try {
            return HttpStatusCode.valueOf(status);
        } catch (final IllegalArgumentException e) {
            logger().debug(
                            "Invalid HTTP status code received {}, will fall back to {}",
                            status,
                            defaultStatusCode,
                            e);
            return requireNonNull(defaultStatusCode);
        }
    }
}
