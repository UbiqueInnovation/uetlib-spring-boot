package ch.ubique.uetlib.spring.boot.problem.handler.trait.violation;

import static java.util.Comparator.*;
import static java.util.Optional.ofNullable;

import ch.ubique.uetlib.core.validator.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import java.util.Comparator;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Violation(
        @JsonProperty(required = true) @NotBlank String fieldName,
        @Nullable @NullOrNotBlank String message,
        @Nullable Object rejectedValue)
        implements Comparable<Violation> {

    // compare by field name, message, rejected value (nulls first)
    private static final Comparator<Violation> COMPARATOR =
            comparing(Violation::fieldName)
                    .thenComparing(nullsFirst(comparing(Violation::message)))
                    .thenComparing(
                            t ->
                                    ofNullable(t.rejectedValue())
                                            .map(Object::hashCode)
                                            .orElse(Integer.MIN_VALUE));

    public Violation(final String fieldName, final @Nullable String message) {
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
    public int compareTo(final Violation o) {
        return COMPARATOR.compare(this, o);
    }
}
