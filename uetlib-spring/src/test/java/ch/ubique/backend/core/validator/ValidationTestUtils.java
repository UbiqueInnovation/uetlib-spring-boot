package ch.ubique.backend.core.validator;

import ch.ubique.backend.core.utils.ValidationUtils;
import ch.ubique.backend.test.assertion.AssertionUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;

import org.junit.jupiter.api.Assertions;

import java.util.Set;
import java.util.stream.Collectors;

public final class ValidationTestUtils {
    private ValidationTestUtils() {}

    public static <T> void assertIsValid(final T object) {
        final var constraintViolations = ValidationUtils.validate(object);
        Assertions.assertTrue(
                constraintViolations.isEmpty(), "Object not valid: " + constraintViolations);
    }

    public static <T> void assertIsNotValid(
            final T object, final Set<String> expectedConstraintViolationMessages) {
        final var constraintViolations = ValidationUtils.validate(object);
        final var constraintViolationMessages =
                constraintViolations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet());
        Assertions.assertEquals(
                expectedConstraintViolationMessages,
                constraintViolationMessages,
                "Object not valid: " + constraintViolations);
    }

}
