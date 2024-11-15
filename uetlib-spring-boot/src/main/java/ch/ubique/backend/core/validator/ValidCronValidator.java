package ch.ubique.backend.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.scheduling.support.CronExpression;

public class ValidCronValidator implements ConstraintValidator<ValidCron, String> {

    @Override
    public boolean isValid(
            final String expression, final ConstraintValidatorContext constraintValidatorContext) {
        return expression == null || CronExpression.isValidExpression(expression);
    }
}
