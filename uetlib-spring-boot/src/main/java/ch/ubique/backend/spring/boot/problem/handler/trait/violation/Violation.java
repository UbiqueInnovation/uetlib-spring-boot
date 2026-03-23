package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

import static java.util.Comparator.*;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

    @Override
    public int compareTo(final @NonNull Violation o) {
        return COMPARATOR.compare(this, o);
    }
}
