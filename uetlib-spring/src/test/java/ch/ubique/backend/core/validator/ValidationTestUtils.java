package ch.ubique.backend.core.validator;

import ch.ubique.backend.core.utils.ValidationUtils;

import org.junit.jupiter.api.Assertions;

public final class ValidationTestUtils {
    private ValidationTestUtils() {}

    public static void assertIsValid(final Object object) {
        final var constraintViolations = ValidationUtils.validate(object);
        Assertions.assertTrue(
                constraintViolations.isEmpty(), "Object not valid: " + constraintViolations);
    }

    public static void assertIsNotValid(final Object object) {
        Assertions.assertFalse(ValidationUtils.isValid(object), "Object is valid");
    }
}
