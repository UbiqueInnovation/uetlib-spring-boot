package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

/**
 * @see MethodArgumentNotValidException
 * @see Violation
 */
public interface MethodArgumentNotValidAdviceTrait extends BaseBindingResultAdviceTrait {

    @Nullable
    default ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var violations = createViolations(ex.getBindingResult());

        return newConstraintViolationProblemDetail(ex, headers, status, violations, request);
    }
}
