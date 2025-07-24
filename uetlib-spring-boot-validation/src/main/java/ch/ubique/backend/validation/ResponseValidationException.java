package ch.ubique.backend.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class ResponseValidationException extends RuntimeException {

    @JsonIgnore
    private final transient  Set<ConstraintViolation<Object>> violations;
    private final Class<?> responseType;

    public ResponseValidationException(
            Set<ConstraintViolation<Object>> violations, final Class<?> responseType) {
        super("Response validation failed for " + responseType.getName());
        this.violations = violations;
        this.responseType = responseType;
    }

    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }

    public Class<?> getResponseType() {
        return responseType;
    }
}
