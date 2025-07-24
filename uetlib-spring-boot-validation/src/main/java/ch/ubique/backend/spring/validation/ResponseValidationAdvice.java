package ch.ubique.backend.spring.validation;

import jakarta.validation.Validator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;
import java.time.Instant;

@RestControllerAdvice
public class ResponseValidationAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseValidationAdvice.class);

    private final Validator validator;
    private final Level logLevel;
    private final ResponseValidationAction action;

    public ResponseValidationAdvice(
            final Validator validator,
            @Value("${ch.ubique.backend.spring.validation.response.fail.log-level:WARN}")
                    final Level logLevel,
            @Value("${ch.ubique.backend.spring.validation.response.fail.action:DISABLE}")
                    final ResponseValidationAction action) {
        this.validator = validator;
        this.logLevel = logLevel;
        this.action = action;
    }

    @Override
    public boolean supports(
            @NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        if (ResponseValidationAction.DISABLE == action) {
            return false;
        }
        // Check if method or class is annotated with @ValidateResponse
        return returnType.hasMethodAnnotation(ValidateResponse.class)
                || returnType.getContainingClass().isAnnotationPresent(ValidateResponse.class);
    }

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
                switch (action) {
                    case LOG -> logger.atLevel(logLevel).log(
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

