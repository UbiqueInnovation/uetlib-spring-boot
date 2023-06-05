package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

class NullOrNotBlankValidatorTest {
    @Test
    void test() {
        ValidationTestUtils.assertIsValid(new Model(null));
        ValidationTestUtils.assertIsValid(new Model("aaaa"));
        ValidationTestUtils.assertIsValid(new Model(" aaaa "));
        ValidationTestUtils.assertIsNotValid(new Model(""));
        ValidationTestUtils.assertIsNotValid(new Model("\t"));
        ValidationTestUtils.assertIsNotValid(new Model(" "));
    }

    @Valid
    record Model(@NullOrNotBlank String s) {}
}
