package ch.ubique.backend.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(
            final String s, final ConstraintValidatorContext constraintValidatorContext) {
        return s == null || StringUtils.isNotBlank(s);
    }
}
