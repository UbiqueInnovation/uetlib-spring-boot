package ch.ubique.backend.spring.boot.problem.handler;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
     * HttpStatusCode, WebRequest)}. Signature has to match the definition exactly
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
