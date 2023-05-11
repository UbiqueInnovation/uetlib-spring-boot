package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

class NullOrNotBlankValidatorTest {
    @Test
    void test() {
        ValidationUtils.assertIsValid(new Model(null));
        ValidationUtils.assertIsValid(new Model("aaaa"));
        ValidationUtils.assertIsValid(new Model(" aaaa "));
        ValidationUtils.assertIsNotValid(new Model(""));
        ValidationUtils.assertIsNotValid(new Model("\t"));
        ValidationUtils.assertIsNotValid(new Model(" "));
    }

    @Valid
    record Model(@NullOrNotBlank String s) {}
}
