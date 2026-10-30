package ch.ubique.uetlib.core.validator;

import ch.ubique.uetlib.core.utils.ValidationUtils;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

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
