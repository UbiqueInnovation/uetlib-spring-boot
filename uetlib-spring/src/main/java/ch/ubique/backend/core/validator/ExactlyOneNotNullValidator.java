package ch.ubique.backend.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.Objects;

public class ExactlyOneNotNullValidator implements ConstraintValidator<ExactlyOneNotNull, Object> {

    private String[] fields;

    @Override
    public void initialize(final ExactlyOneNotNull atLeastOneNotNull) {
        fields = atLeastOneNotNull.fields();
    }

    @Override
    public boolean isValid(
            final Object object, final ConstraintValidatorContext constraintValidatorContext) {

        final var beanWrapper = new BeanWrapperImpl(object);

        final var nonNullCount =
                Arrays.stream(fields)
                        .map(beanWrapper::getPropertyValue)
                        .filter(Objects::nonNull)
                        .count();
        return nonNullCount == 1;
    }
}
