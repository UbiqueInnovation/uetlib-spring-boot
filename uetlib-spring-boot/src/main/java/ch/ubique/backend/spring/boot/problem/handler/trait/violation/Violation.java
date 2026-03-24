package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import static java.util.Comparator.*;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.ConstraintViolation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Comparator;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Violation(
        @JsonProperty(required = true) String fieldName,
        @JsonProperty(required = true) String message,
        @Nullable Object rejectedValue)
        implements Comparable<Violation> {

    // compare by field name, message, rejected value (nulls first)
    private static final Comparator<Violation> COMPARATOR =
            comparing(Violation::fieldName)
                    .thenComparing(Violation::message)
                    .thenComparing(
                            t ->
                                    ofNullable(t.rejectedValue())
                                            .map(Object::hashCode)
                                            .orElse(Integer.MIN_VALUE));

    public Violation(final String fieldName, final String message) {
        this(fieldName, message, null);
    }

    public static Violation from(final ConstraintViolation<?> violation) {
        final var fieldName = violation.getPropertyPath().toString();
        final var message = violation.getMessage();
        final var rejectedValue = violation.getInvalidValue();
        return new Violation(fieldName, message, rejectedValue);
    }

    public static Violation from(final FieldError error) {
        final var fieldName = error.getField();
        final var message = error.getDefaultMessage();
        final var rejectedValue = error.getRejectedValue();
        return new Violation(fieldName, message, rejectedValue);
    }

    public static Violation from(final ObjectError error) {
        final var fieldName = error.getObjectName();
        final var message = error.getDefaultMessage();
        return new Violation(fieldName, message);
    }

    @Override
    public int compareTo(final @NonNull Violation o) {
        return COMPARATOR.compare(this, o);
    }
}
