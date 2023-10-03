package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

import java.util.Set;

class NullOrNotBlankValidatorTest {
    @Test
    void test() {
        ValidationTestUtils.assertIsValid(new Model(null));
        ValidationTestUtils.assertIsValid(new Model("aaaa"));
        ValidationTestUtils.assertIsValid(new Model(" aaaa "));

        ValidationTestUtils.assertIsNotValid(
                new Model(""), Set.of("value '' must be null or not blank"));
        ValidationTestUtils.assertIsNotValid(
                new Model("\t"), Set.of("value '\t' must be null or not blank"));
        ValidationTestUtils.assertIsNotValid(
                new Model(" "), Set.of("value ' ' must be null or not blank"));
    }

    @Valid
    record Model(@NullOrNotBlank String s) {}
}
