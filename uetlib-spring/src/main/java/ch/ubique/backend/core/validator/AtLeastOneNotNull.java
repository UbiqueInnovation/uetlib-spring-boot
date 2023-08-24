package ch.ubique.backend.core.validator;

import static ch.ubique.backend.core.validator.AtLeastOneNotNull.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(List.class)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
@Documented
public @interface AtLeastOneNotNull {

    String message() default "At least one of the provided fields must be non-null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Fields to validate */
    String[] fields();

    /**
     * Defines several {@link AtLeastOneNotNull} annotations on the same element.
     *
     * @see AtLeastOneNotNull
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface List {

        AtLeastOneNotNull[] value();
    }
}
