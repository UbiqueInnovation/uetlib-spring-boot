package ch.ubique.backend.core.validator;

import jakarta.validation.Valid;

import org.junit.jupiter.api.Test;

import java.util.Set;

class ValidCronValidatorTest {
    @Test
    void test() {
        ValidationTestUtils.assertIsValid(new Model(null));
        ValidationTestUtils.assertIsValid(new Model("0 0 2 * * ?"));
        ValidationTestUtils.assertIsValid(new Model("0 0 2 * * *"));

        ValidationTestUtils.assertIsNotValid(
                new Model(""), Set.of("value '' must be a valid cron expression"));
        ValidationTestUtils.assertIsNotValid(
                new Model("0 0 2 * * ? *"),
                Set.of("value '0 0 2 * * ? *' must be a valid cron expression"));
    }

    @Valid
    record Model(@ValidCron String cron) {}
}
