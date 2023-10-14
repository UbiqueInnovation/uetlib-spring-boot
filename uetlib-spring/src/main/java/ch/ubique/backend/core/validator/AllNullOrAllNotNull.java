package ch.ubique.backend.core.validator;

import static ch.ubique.backend.core.validator.AllNullOrAllNotNull.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(List.class)
@Constraint(validatedBy = AllNullOrAllNotNullValidator.class)
@Documented
@NotNull
public @interface AllNullOrAllNotNull {

    String message() default "Either all or exactly none the provided fields must be non-null. Fields: '{fields}'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Fields to validate */
    String[] fields();

    /**
     * Defines several {@link AllNullOrAllNotNull} annotations on the same element.
     *
     * @see AllNullOrAllNotNull
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface List {

        AllNullOrAllNotNull[] value();
    }
}
