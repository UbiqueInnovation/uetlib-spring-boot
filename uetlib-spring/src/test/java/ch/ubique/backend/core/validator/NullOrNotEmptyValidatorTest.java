package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

import java.util.List;

class NullOrNotEmptyValidatorTest {
    @Test
    void test() {
        ValidationUtils.assertIsValid(new Model(null));
        ValidationUtils.assertIsValid(new Model(List.of(1)));
        ValidationUtils.assertIsNotValid(new Model(List.of()));
    }

    @Valid
    record Model(@NullOrNotEmpty List<Integer> list) {}
}
