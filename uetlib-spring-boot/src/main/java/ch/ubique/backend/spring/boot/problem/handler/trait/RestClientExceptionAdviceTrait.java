package ch.ubique.backend.spring.boot.problem.handler.trait;

import static java.util.Objects.requireNonNullElse;

import ch.ubique.backend.spring.boot.problem.handler.UetlibExceptionHandlerSkeletonTrait;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public interface RestClientExceptionAdviceTrait extends UetlibExceptionHandlerSkeletonTrait {

    /**
     * Handle all exceptions raised within Spring MVC handling of the request. This is an extension
     * to {@link ResponseEntityExceptionHandler#handleException(Exception, WebRequest)} for more
     * exceptions.
     *
     * @param ex the exception to handle
     * @param request the current request
     */
    // add more here
    @ExceptionHandler(HttpStatusCodeException.class)
    @Nullable
    default ResponseEntity<Object> handleHttpStatusCode(
            final Exception ex, final WebRequest request) throws Exception {
        final var headers = new HttpHeaders();
        return switch (ex) {
            case final HttpStatusCodeException subEx ->
                    handleHttpStatusCode(subEx, headers, request);
            default -> throw ex;
        };
    }

    /**
     * Customise the handling of {@link HttpStatusCodeException}.
     *
     * <p>By default, this method creates a {@link ProblemDetail} with the status and a short detail
     * message, and also looks up an override for the detail via {@link MessageSource}, before
     * delegating to {@link #handleExceptionInternal}.
     *
     * @param ex the exception to handle
     * @param headers the headers to use for the response
     * @param request the current request
     * @return a {@code ResponseEntity} for the response to use, possibly {@code null} when the
     *     response is already committed
     */
    @Nullable
    default ResponseEntity<Object> handleHttpStatusCode(
            final HttpStatusCodeException ex, final HttpHeaders headers, final WebRequest request) {
        final var status = ex.getStatusCode();
        final var body =
                createProblemDetail(
                        ex,
                        status,
                        requireNonNullElse(ex.getLocalizedMessage(), "Status Code Exception"),
                        null,
                        null,
                        request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
