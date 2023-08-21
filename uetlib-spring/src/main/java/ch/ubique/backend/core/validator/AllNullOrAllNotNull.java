package ch.ubique.backend.core.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllNullOrAllNotNullValidator.class)
@Documented
public @interface AllNullOrAllNotNull {

    String message() default "Either all or exactly none the provided fields must be non-null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Fields to validate */
    String[] fields();
}
