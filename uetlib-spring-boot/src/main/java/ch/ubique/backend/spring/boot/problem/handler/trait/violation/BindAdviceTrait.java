package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @see BindException
 * @see Violation
 */
public interface BindAdviceTrait extends BaseBindingResultAdviceTrait {

    @ExceptionHandler(BindException.class)
    @Nullable
    @SuppressWarnings("java:S112")
    default ResponseEntity<Object> handleBindException(final Exception ex, final WebRequest request)
            throws Exception {
        final var headers = new HttpHeaders();
        return switch (ex) {
            case final BindException subEx ->
                    handleBind(subEx, headers, HttpStatus.BAD_REQUEST, request);
            default -> throw ex;
        };
    }

    @Nullable
    default ResponseEntity<Object> handleBind(
            final BindException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var violations = createViolations(ex);

        return newConstraintViolationProblemDetail(ex, headers, status, violations, request);
    }
}
