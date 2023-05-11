package ch.ubique.backend.core.validator;

import static java.lang.annotation.ElementType.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
@Documented
public @interface NullOrNotEmpty {

    String message() default "must be null or not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
