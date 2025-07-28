package ch.ubique.backend.spring.validation;

import jakarta.validation.Validator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;
import java.time.Instant;

/**
 * Automatically validates HTTP response objects using Jakarta Bean Validation.
 *
 * <p>Intercepts responses from controllers annotated with {@link ValidateResponse} and performs
 * validation based on configured {@link ResponseValidationAction}. Logs validation execution time
 * at DEBUG level for performance monitoring.
 *
 * @see ValidateResponse
 * @see ResponseValidationException
 * @see ResponseValidationAction
 * @author Ubique Innovation AG
 * @since 1.4.0
 */
@RestControllerAdvice
public class ResponseValidationAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseValidationAdvice.class);

    /** The Jakarta Bean Validation validator used to validate response objects. */
    private final Validator validator;

    /** Response validation properties to define log level and action. */
    private final ResponseValidationProperties properties;

    /**
     * Constructs ResponseValidationAdvice with configuration from application properties.
     *
     * @param validator the Jakarta Bean Validation validator
     */
    public ResponseValidationAdvice(
            final Validator validator, ResponseValidationProperties properties) {
        this.validator = validator;
        this.properties = properties;
    }

    /**
     * Determines if validation should be applied based on {@link ValidateResponse} annotation and
     * configured action.
     *
     * @param returnType the return type of the controller method
     * @param converterType the selected converter type for the response
     * @return {@code true} if this advice should be applied, {@code false} otherwise
     */
    @Override
    public boolean supports(
            @NotNull MethodParameter returnType,
            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        if (ResponseValidationAction.DISABLE == properties.getAction()) {
            return false;
        }
        // Check if method or class is annotated with @ValidateResponse
        return returnType.hasMethodAnnotation(ValidateResponse.class)
                || returnType.getContainingClass().isAnnotationPresent(ValidateResponse.class);
    }

    /**
     * Validates response body using Jakarta Bean Validation and takes configured action. Logs
     * validation time at DEBUG level for performance monitoring.
     *
     * @param body the response body to validate (may be {@code null})
     * @param returnType the return type of the controller method
     * @param selectedContentType the content type selected for the response
     * @param selectedConverterType the converter type selected for the response
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @return the original response body (unchanged)
     * @throws ResponseValidationException if validation fails and action is {@link
     *     ResponseValidationAction#THROW}
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response) {
        if (body != null) {
            final var start = Instant.now();
            final var violations = validator.validate(body);
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "validation took: {}ms", Duration.between(start, Instant.now()).toMillis());
            }
            if (!violations.isEmpty()) {
                switch (properties.getAction()) {
                    case LOG ->
                            logger.atLevel(properties.getLogLevel())
                                    .log(
                                            "Response validation failed for {}: {}",
                                            body.getClass().getName(),
                                            violations);
                    case THROW ->
                            throw new ResponseValidationException(violations, body.getClass());
                    case DISABLE -> {
                        // Do nothing, just skip validation
                    }
                }
            }
        }
        return body;
    }
}
