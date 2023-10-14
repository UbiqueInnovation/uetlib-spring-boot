package ch.ubique.backend.core.validator;

import org.junit.jupiter.api.Test;

import java.util.Set;

@SuppressWarnings("java:S5961")
class ExactlyOneNotNullValidatorTest {
    @Test
    void testType() {
        ValidationTestUtils.assertIsValid(new AnnotatedType(1, null, 3, null));
        ValidationTestUtils.assertIsValid(new AnnotatedType(1, null, null, 4));
        ValidationTestUtils.assertIsValid(new AnnotatedType(null, 2, null, 4));
        ValidationTestUtils.assertIsValid(new AnnotatedType(null, 2, 3, null));
        ValidationTestUtils.assertIsValid(new AnnotatedType(null, 2, null, 4));

        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, null, null, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'.",
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, null, null, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, 2, null, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, null, 3, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, null, null, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, 2, null, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'.",
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, null, 3, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'.",
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, 2, 3, null),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, 2, null, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, null, 3, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(null, 2, 3, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));
        ValidationTestUtils.assertIsNotValid(
                new AnnotatedType(1, 2, 3, 4),
                Set.of(
                        "Exactly one of the provided fields must be non-null. Fields: '[prop1, prop2]'.",
                        "Exactly one of the provided fields must be non-null. Fields: '[prop3, prop4]'."));

        // the following are non-annotated and thus all valid (sanity check)
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, null, null, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, null, null, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, 2, null, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, null, 3, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, null, null, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, 2, null, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, null, 3, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, null, null, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, 2, null, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, 2, 3, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, 2, null, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, null, 3, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, 2, 3, null));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, 2, null, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, null, 3, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(null, 2, 3, 4));
        ValidationTestUtils.assertIsValid(new NonAnnotatedType(1, 2, 3, 4));
    }

    @ExactlyOneNotNull(fields = {"prop1", "prop2"})
    @ExactlyOneNotNull(fields = {"prop3", "prop4"})
    record AnnotatedType(Integer prop1, Integer prop2, Integer prop3, Integer prop4) {}

    record NonAnnotatedType(Integer prop1, Integer prop2, Integer prop3, Integer prop4) {}
}
