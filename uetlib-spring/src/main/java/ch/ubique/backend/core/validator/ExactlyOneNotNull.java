package ch.ubique.backend.core.validator;

import static ch.ubique.backend.core.validator.ExactlyOneNotNull.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(List.class)
@Constraint(validatedBy = ExactlyOneNotNullValidator.class)
@Documented
public @interface ExactlyOneNotNull {

    String message() default "Exactly one of the provided fields must be non-null. Fields: '{fields}'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Fields to validate */
    String[] fields();

    /**
     * Defines several {@link ExactlyOneNotNull} annotations on the same element.
     *
     * @see ExactlyOneNotNull
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ExactlyOneNotNull[] value();
    }
}
