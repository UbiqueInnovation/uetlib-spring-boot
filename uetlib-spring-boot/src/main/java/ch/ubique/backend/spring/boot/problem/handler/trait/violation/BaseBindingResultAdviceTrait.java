package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import org.jspecify.annotations.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.stream.Stream;

public interface BaseBindingResultAdviceTrait extends BaseViolationAdviceTrait {

    default Violation createViolation(final FieldError error) {
        final var fieldName = error.getField();
        final var rejectedValue = error.getRejectedValue();
        return new Violation(fieldName, error.getDefaultMessage(), rejectedValue);
    }

    default Violation createViolation(final ObjectError error) {
        final var fieldName = error.getObjectName();
        return new Violation(fieldName, error.getDefaultMessage());
    }

    default Stream<Violation> createViolations(final @Nullable BindingResult result) {
        if (result == null) {
            return Stream.empty();
        }

        final var fieldErrors = result.getFieldErrors().stream().map(this::createViolation);
        final var globalErrors = result.getGlobalErrors().stream().map(this::createViolation);
        return Stream.concat(fieldErrors, globalErrors);
    }
}
