package ch.ubique.backend.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

public class NullOrNotEmptyValidator implements ConstraintValidator<NullOrNotEmpty, Collection<?>> {

    @Override
    public boolean isValid(
            final Collection<?> collection,
            final ConstraintValidatorContext constraintValidatorContext) {
        return collection == null || CollectionUtils.isNotEmpty(collection);
    }
}
