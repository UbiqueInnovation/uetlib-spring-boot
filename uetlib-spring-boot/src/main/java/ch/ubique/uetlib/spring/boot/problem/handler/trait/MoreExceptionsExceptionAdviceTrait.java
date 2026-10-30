package ch.ubique.uetlib.spring.boot.problem.handler.trait;

import static java.util.Objects.requireNonNullElse;

import ch.ubique.uetlib.core.exception.ResourceNotFoundException;
import ch.ubique.uetlib.spring.boot.problem.handler.UetlibExceptionHandlerSkeletonTrait;
import java.util.NoSuchElementException;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public interface MoreExceptionsExceptionAdviceTrait extends UetlibExceptionHandlerSkeletonTrait {

    /**
     * Handle all exceptions raised within Spring MVC handling of the request. This is an extension
     * to {@link ResponseEntityExceptionHandler#handleException(Exception, WebRequest)} for more
     * exceptions.
     *
     * @param ex the exception to handle
     * @param request the current request
     */
    // add more here
    @ExceptionHandler({NoSuchElementException.class, ResourceNotFoundException.class})
    @Nullable
    @SuppressWarnings("java:S112")
    default ResponseEntity<Object> handleMoreExceptions(
            final Exception ex, final WebRequest request) throws Exception {
        final var headers = new HttpHeaders();
        return switch (ex) {
            case final NoSuchElementException subEx -> handleNoSuchElement(subEx, headers, request);
            case final ResourceNotFoundException subEx ->
                    handleResourceNotFound(subEx, headers, request);
            default -> throw ex;
        };
    }

    /**
     * Customize the handling of {@link NoSuchElementException}.
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
    default ResponseEntity<Object> handleNoSuchElement(
            final NoSuchElementException ex, final HttpHeaders headers, final WebRequest request) {
        final var status = HttpStatus.NOT_FOUND;
        final var body =
                createProblemDetail(
                        ex,
                        status,
                        requireNonNullElse(ex.getLocalizedMessage(), "No such element"),
                        null,
                        null,
                        request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Customize the handling of {@link ResourceNotFoundException}.
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
    default ResponseEntity<Object> handleResourceNotFound(
            final ResourceNotFoundException ex,
            final HttpHeaders headers,
            final WebRequest request) {
        final var status = HttpStatus.NOT_FOUND;
        final var body =
                createProblemDetail(
                        ex,
                        status,
                        requireNonNullElse(ex.getLocalizedMessage(), "Resource not found"),
                        null,
                        null,
                        request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
