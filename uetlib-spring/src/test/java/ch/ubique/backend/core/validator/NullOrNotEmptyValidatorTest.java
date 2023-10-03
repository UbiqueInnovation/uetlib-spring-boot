package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class NullOrNotEmptyValidatorTest {
    @Test
    void test() {
        ValidationTestUtils.assertIsValid(new Model(null));
        ValidationTestUtils.assertIsValid(new Model(List.of(1)));

        ValidationTestUtils.assertIsNotValid(
                new Model(List.of()), Set.of("value '[]' must be null or not empty"));
    }

    @Valid
    record Model(@NullOrNotEmpty List<Integer> list) {}
}
