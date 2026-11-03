package ch.ubique.uetlib.spring.boot.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

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
@SuppressWarnings("java:S110")
public class ResponseValidationException extends ConstraintViolationException {

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
        super("Response validation failed for " + responseType.getName(), violations);
        this.responseType = responseType;
    }

    /**
     * Returns the response object type that failed validation.
     *
     * @return the response object type, never {@code null}
     */
    public Class<?> responseType() {
        return responseType;
    }
}
