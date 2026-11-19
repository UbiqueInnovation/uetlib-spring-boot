package ch.uetlib.spring.boot.problem.handler.trait.violation;

import ch.uetlib.spring.boot.validation.ResponseValidationException;

import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Stream;

/** Advice trait to handle any validation exceptions */
public interface ViolationAdviceTrait extends ViolationProblemDetailTrait {

    @ExceptionHandler({
        ResponseValidationException.class,
        ConstraintViolationException.class,
        BindException.class
    })
    @Nullable
    @SuppressWarnings("java:S112")
    default ResponseEntity<Object> handleViolationExceptions(
            final Exception ex, final WebRequest request) throws Exception {
        final var headers = new HttpHeaders();
        return switch (ex) {
            // ResponseValidationException is a subclass of ConstraintViolationException, but a
            // server error, not a client error
            case final ResponseValidationException subEx ->
                    handleConstraintViolation(
                            subEx, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
            case final ConstraintViolationException subEx ->
                    handleConstraintViolation(subEx, headers, HttpStatus.BAD_REQUEST, request);
            case final BindException subEx ->
                    handleBind(subEx, headers, HttpStatus.BAD_REQUEST, request);
            default -> throw ex;
        };
    }

    @Nullable
    default ResponseEntity<Object> handleConstraintViolation(
            final ConstraintViolationException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var includeConstraintViolations =
                getUetlibSpringBootProblemProperties().isIncludeConstraintViolations();
        final var violations = includeConstraintViolations ? createViolations(ex) : null;

        return newConstraintViolationProblemDetail(ex, headers, status, violations, request);
    }

    /**
     * the exception handler for {@link MethodArgumentNotValidException} is in {@link
     * ResponseEntityExceptionHandler}
     */
    @Nullable
    default ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {
        final var violations = createViolations(ex.getBindingResult()).toList();

        final var includeConstraintViolations =
                getUetlibSpringBootProblemProperties().isIncludeConstraintViolations();

        return newConstraintViolationProblemDetail(
                ex,
                headers,
                status,
                includeConstraintViolations ? violations.stream() : null,
                request);
    }

    @Nullable
    default ResponseEntity<Object> handleBind(
            final BindException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var includeConstraintViolations =
                getUetlibSpringBootProblemProperties().isIncludeConstraintViolations();
        final var violations = includeConstraintViolations ? createViolations(ex) : null;

        return newConstraintViolationProblemDetail(ex, headers, status, violations, request);
    }

    default Stream<Violation> createViolations(final ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream().map(Violation::from);
    }

    default Stream<Violation> createViolations(final @Nullable BindingResult result) {
        if (result == null) {
            return Stream.empty();
        }

        final var fieldErrors = result.getFieldErrors().stream().map(Violation::from);
        final var globalErrors = result.getGlobalErrors().stream().map(Violation::from);
        return Stream.concat(fieldErrors, globalErrors);
    }
}
