package ch.ubique.backend.core.validator;

import static ch.ubique.backend.core.validator.NullOrNotEmpty.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
@Documented
public @interface NullOrNotEmpty {

    String message() default "value '${validatedValue}' must be null or not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link NullOrNotEmpty} annotations on the same element.
     *
     * @see NullOrNotEmpty
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NullOrNotEmpty[] value();
    }
}
