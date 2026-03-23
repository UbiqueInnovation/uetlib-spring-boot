package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Stream;

/**
 * @see ConstraintViolationException
 * @see Violation
 */
public interface ConstraintViolationAdviceTrait extends BaseViolationAdviceTrait {

    @ExceptionHandler(ConstraintViolationException.class)
    @Nullable
    @SuppressWarnings("java:S112")
    default ResponseEntity<Object> handleConstraintViolation(
            final Exception ex, final WebRequest request) throws Exception {
        final var headers = new HttpHeaders();
        return switch (ex) {
            case final ConstraintViolationException subEx ->
                    handleConstraintViolation(subEx, headers, HttpStatus.BAD_REQUEST, request);
            default -> throw ex;
        };
    }

    default Violation createViolation(final ConstraintViolation<?> violation) {
        return new Violation(violation.getPropertyPath().toString(), violation.getMessage(), violation.getInvalidValue());
    }

    default Stream<Violation> createViolations(final ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream().map(this::createViolation);
    }

    @Nullable
    default ResponseEntity<Object> handleConstraintViolation(
            final ConstraintViolationException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var violations = createViolations(ex);

        return newConstraintViolationProblemDetail(ex, headers, status, violations, request);
    }
}
