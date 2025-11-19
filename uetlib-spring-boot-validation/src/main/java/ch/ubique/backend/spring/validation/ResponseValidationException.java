package ch.ubique.backend.spring.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

/**
 * Exception thrown when response validation fails.
 *
 * <p>Thrown by {@link ResponseValidationAdvice} when action is {@link
 * ResponseValidationAction#THROW} and a response object fails Jakarta Bean Validation constraints.
 * Provides access to constraint violations and response type for error handling.
 *
 * @see ResponseValidationAdvice
 * @see ValidateResponse
 * @see ResponseValidationAction#THROW
 * @author Ubique Innovation AG
 * @since 1.4.0
 */
public class ResponseValidationException extends RuntimeException {

    /** The constraint violations that caused this exception. */
    @JsonIgnore private final transient Set<ConstraintViolation<Object>> violations;

    /** The response object type that failed validation. */
    private final Class<?> responseType;

    /**
     * Constructs ResponseValidationException with violations and response type.
     *
     * @param violations the constraint violations that occurred
     * @param responseType the response object type that failed validation
     */
    public ResponseValidationException(
            final Set<ConstraintViolation<Object>> violations, final Class<?> responseType) {
        super("Response validation failed for " + responseType.getName());
        this.violations = violations;
        this.responseType = responseType;
    }

    /**
     * Returns the constraint violations that caused this exception.
     *
     * @return the constraint violations, never {@code null}
     */
    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }

    /**
     * Returns the response object type that failed validation.
     *
     * @return the response object type, never {@code null}
     */
    public Class<?> getResponseType() {
        return responseType;
    }
}
