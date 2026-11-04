package ch.uetlib.spring.boot.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables automatic response validation for REST controllers.
 *
 * <p>Can be applied to controller classes (all methods) or individual methods. Response objects are
 * validated using Jakarta Bean Validation before serialization. Behavior controlled by {@code
 * ch.uetlib.spring.validation.response.fail.action}.
 *
 * @see ResponseValidationAdvice
 * @see ResponseValidationException
 * @see ResponseValidationAction
 * @author Ubique Innovation AG
 * @since 1.4.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateResponse {}
